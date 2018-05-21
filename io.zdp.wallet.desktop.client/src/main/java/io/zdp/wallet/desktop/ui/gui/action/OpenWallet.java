package io.zdp.wallet.desktop.ui.gui.action;

import java.awt.FileDialog;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.util.concurrent.TimeUnit;

import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.api.service.ApiService;
import io.zdp.wallet.desktop.ui.common.Alert;
import io.zdp.wallet.desktop.ui.common.SynchronousJFXFileChooser;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
public class OpenWallet {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MainWindow mainWindow;

	@Autowired
	private ApiService walletService;

	public void open(Window parent, JDialog dialog) {

		javafx.embed.swing.JFXPanel dummy = new javafx.embed.swing.JFXPanel();
		Platform.setImplicitExit(false);

		SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
			FileChooser ch = new FileChooser();

			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ZDP Wallet (*.zdp)", "*.zdp");
			ch.getExtensionFilters().add(extFilter);

			ch.setTitle("Open wallet file");
			return ch;
		});

		File walletFile = chooser.showOpenDialog();

		if (walletFile == null || false == walletFile.canRead()) {
			return;
		}

		log.debug("Open wallet: " + walletFile);

		Wallet wallet = null;

		try {
			wallet = walletService.openWallet(walletFile, "");
		} catch (Exception e) {
			log.error("Error: ", e);
		}

		if (wallet != null) {

			mainWindow.setWallet(wallet, walletFile);

			if (dialog != null) {
				dialog.dispose();
			}

		} else {
			Alert.error("Sorry, this wallet can not be loaded");
		}

	}
}
