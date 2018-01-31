package io.zdp.wallet.desktop.ui.gui.action;

import java.awt.FileDialog;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JDialog;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;
import io.zdp.wallet.desktop.ui.common.Alert;
import io.zdp.wallet.desktop.ui.common.I18n;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.EnterPasswordPanel;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class OpenWallet {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private I18n i18n;

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	public void open(Window parent, JDialog dialog) {

		FileDialog fileDialog = new FileDialog(mainWindow.getFrame(), "Open Wallet", FileDialog.LOAD);

		fileDialog.setFilenameFilter(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".wallet");
			}
		});

		fileDialog.setVisible(true);

		File walletFile = new File(fileDialog.getDirectory() + "" + fileDialog.getFile());

		if (fileDialog.getDirectory() == null || fileDialog.getFile() == null) {
			return;
		}

		log.debug("Open wallet: " + walletFile);

		// /if (walletService.load(file, pass))

		EnterPasswordPanel passwordPanel = new EnterPasswordPanel();
		new QTextComponentContextMenu(passwordPanel.password);

		JDialog passwordDialog = SwingHelper.dialog(parent, passwordPanel);

		passwordDialog.setTitle("Enter password");

		ActionListener al = ev -> {

			String pass = new String(passwordPanel.password.getPassword());

			if (StringUtils.isEmpty(pass)) {
				return;
			}

			Wallet wallet = WalletService.load(walletFile, pass);

			if (wallet != null) {

				mainWindow.setWallet(wallet, walletFile, pass);

				passwordDialog.dispose();
				// startDialog.dispose();

				if (dialog != null) {
					dialog.dispose();
				}

			} else {
				Alert.error("Sorry, this wallet can not be loaded");
			}

		};

		passwordPanel.btnOk.addActionListener(al);
		passwordPanel.password.addActionListener(al);

		SwingHelper.installEscapeCloseOperation(passwordDialog);

		passwordDialog.setVisible(true);
	}
}
