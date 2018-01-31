package io.zdp.wallet.desktop.ui.gui.action;

import java.awt.FileDialog;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.swing.JDialog;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;
import io.zdp.wallet.desktop.ui.common.Alert;
import io.zdp.wallet.desktop.ui.common.I18n;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.PasswordPanel;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class CreateNewWallet {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private I18n i18n;

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	public void create(Window parent) {

		PasswordPanel passwordPanel = new PasswordPanel();

		new QTextComponentContextMenu(passwordPanel.password);
		new QTextComponentContextMenu(passwordPanel.passwordConfirm);

		JDialog passwordDialog = SwingHelper.dialog(parent, passwordPanel);
		passwordDialog.setTitle("Enter password");

		passwordPanel.btnOk.addActionListener(ev -> {

			char[] pass = passwordPanel.password.getPassword();

			if (ArrayUtils.isEmpty(pass)) {
				return;
			}

			char[] confirm = passwordPanel.passwordConfirm.getPassword();
			if (false == Arrays.equals(confirm, pass)) {
				Alert.warn("Passwords do not match!");
				return;
			}

			FileDialog fileDialog = new FileDialog(mainWindow.getFrame(), "Save Wallet", FileDialog.SAVE);
			fileDialog.setFilenameFilter(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".wallet");
				}
			});
			fileDialog.setFile("my_new.wallet");
			fileDialog.setVisible(true);

			File walletFile = new File(fileDialog.getDirectory() + "" + fileDialog.getFile());

			if (fileDialog.getDirectory() == null || fileDialog.getFile() == null) {
				return;
			}

			passwordDialog.dispose();

			log.debug("Save new wallet: " + walletFile);

			try {

				String seed = CryptoUtils.generateRandomNumber(512);

				Wallet w = WalletService.create(seed, walletFile);
				
				walletService.setCurrentWallet(w, walletFile, pass);

				mainWindow.setWallet(w, walletFile, pass);

				if (parent != mainWindow.getFrame()) {
					parent.dispose();
				}

				Alert.info("New wallet was created!");

			} catch (Exception e) {
				log.error("Error: ", e);
			}

		});

		SwingHelper.installEscapeCloseOperation(passwordDialog);

		passwordDialog.setVisible(true);
	}

}
