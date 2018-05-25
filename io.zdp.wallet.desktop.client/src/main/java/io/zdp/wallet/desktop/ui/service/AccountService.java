package io.zdp.wallet.desktop.ui.service;

import java.awt.TrayIcon.MessageType;
import java.math.BigDecimal;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.GetBalanceResponse;
import io.zdp.client.ZdpClient;
import io.zdp.crypto.Curves;
import io.zdp.wallet.api.db.domain.AccountTransaction;
import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.gui.MainWindow;

@Service
public class AccountService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Value("${api.central.url}")
	private String apiCentralUrl;

	@Value("${api.url.address.new}")
	private String apiUrlAddressNew;

	@Autowired
	private MainWindow mainWindow;

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private ZdpClient zdp;

	@PostConstruct
	public void init() {
	}

	public void sync(Runnable postSyncFunction) {

		SwingHelper.async(mainWindow.getFrame(), "Synchronizing wallet", () -> {

			try {

				// Get balance
				Wallet wallet = walletService.getCurrentWallet();
				/*
				GetBalanceResponse balance = zdp.getBalance(wallet.getPrivateKey(),Curves.DEFAULT_CURVE);
				wallet.setBalance(new BigDecimal(balance.getAmount()));
				*/

				// Get transactions
				/*
				ListTransactionsResponse transactions = zdp.getTransactions(wallet.getPrivateKey(), 0, 100);

				if (transactions != null && transactions.getTransactions().isEmpty() == false) {

					for (Transaction details : transactions.getTransactions()) {

						if (null == wallet.getTxByUuid(details.getUuid())) {

							WalletTransaction tx = new WalletTransaction();

							tx.setAmount(new BigDecimal(details.getAmount()));
							tx.setDate(details.getDate());
							tx.setUuid(details.getUuid());

							wallet.getTransactions().add(tx);
						}
					}
				}
				*/

				mainWindow.showSystemTrayMessage(MessageType.INFO, "Wallet synchronized");

				if (postSyncFunction != null) {
					postSyncFunction.run();
				}

			} catch (Exception e) {
				log.error("Error: ", e);
			}
		});
	}

}
