package io.zdp.wallet.desktop.ui.job;

import java.awt.TrayIcon.MessageType;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.api.model.BalanceResponse;
import io.zdp.client.ZdpClient;
import io.zdp.common.crypto.Signer;
import io.zdp.wallet.desktop.api.domain.MyAddress;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class UpdateWalletBalances {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ZdpClient zdp;

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	private Map<String, PrivateKey> cachedKeys = new HashMap<>();

	public void update() throws Exception {

		if (walletService.getCurrentWallet() != null) {

			List<MyAddress> myAddresses = walletService.getCurrentWallet().getMyAddresses();

			if (myAddresses.isEmpty() == false) {

				log.debug("Update addresses balances");

				final Map<String, PrivateKey> map = new HashMap<>();

				for (MyAddress addr : myAddresses) {

					final String address = addr.getAddress();

					if (cachedKeys.containsKey(address)) {

						map.put(address, cachedKeys.get(address));

					} else {

						PrivateKey key = Signer.generatePrivateKey(addr.getPrivateKey());

						map.put(address, key);

						cachedKeys.put(address, key);

					}

				}

				final List<BalanceResponse> addressesBalances = zdp.getAddressesBalances(map);

				for (final BalanceResponse b : addressesBalances) {
					final MyAddress addr = walletService.getCurrentWallet().getMyAddressByUuid(b.getAddress());
					final double currentBalance = addr.getBalance();
					final double newBalance = b.getBalance();
					if (newBalance != currentBalance) {
						addr.setBalance(newBalance);
						mainWindow.showSystemTrayMessage(MessageType.INFO, "Balance updated for: " + addr.getAddress());
					}
				}

			}
		}
	}

}
