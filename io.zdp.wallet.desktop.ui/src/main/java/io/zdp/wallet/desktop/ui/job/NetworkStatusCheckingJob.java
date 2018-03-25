package io.zdp.wallet.desktop.ui.job;

import java.awt.TrayIcon.MessageType;
import java.math.BigDecimal;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.zdp.api.model.v1.GetBalanceResponse;
import io.zdp.client.ZdpClient;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.desktop.ui.common.Icons;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class NetworkStatusCheckingJob {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MainWindow mainWindow;

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private ZdpClient zdp;

	private boolean connected;

	@Scheduled(fixedDelay = DateUtils.MILLIS_PER_SECOND * 20)
	public void check() throws Exception {

		if (connected == false) {
			ImageIcon icon = new ImageIcon(this.getClass().getResource("/icons/ajax-loader.gif"));
			mainWindow.setStatusMessage("Checking network connection", icon);
		}

		try {

			final Wallet wallet = walletService.getCurrentWallet();

			if (wallet != null) {

				GetBalanceResponse accountBalance = zdp.getBalance(wallet.getPrivateKey(), wallet.getPublicKey());

				if (false == wallet.getBalance().toPlainString().equals(accountBalance.getAmount())) {
					wallet.setBalance(new BigDecimal(accountBalance.getAmount()));
					mainWindow.showSystemTrayMessage(MessageType.INFO, "Wallet balance changed");
					SwingUtilities.invokeLater(() -> {
						mainWindow.updateUI();
					});
				}

			} else {

				zdp.ping();

			}

			connected = true;

			mainWindow.setStatusMessage("Connected to network", Icons.getIcon("check.png"));

		} catch (Exception e) {
			log.error("Error: " + e.getMessage());
			mainWindow.setStatusMessage("Network not available...", Icons.getIcon("cancel.png"));
			connected = false;
		}

		Thread.sleep(DateUtils.MILLIS_PER_SECOND * RandomUtils.nextInt(1, 3));
	}

	public boolean isConnected() {
		return connected;
	}

}
