package io.zdp.wallet.desktop.ui.job;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.zdp.client.ZdpClient;
import io.zdp.wallet.desktop.ui.common.Icons;
import io.zdp.wallet.desktop.ui.gui.MainWindow;

@Component
public class NetworkStatusCheckingJob {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MainWindow mainWindow;

	@Autowired
	private ZdpClient zdp;

	private boolean connected;

	@Scheduled(fixedDelay = DateUtils.MILLIS_PER_SECOND * 5)
	public void check() throws Exception {

		if (connected == false) {
			ImageIcon icon = new ImageIcon(this.getClass().getResource("/icons/ajax-loader.gif"));
			mainWindow.setStatusMessage("Checking network connection", icon);
		}

		try {
			zdp.ping();
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
