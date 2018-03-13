package io.zdp.wallet.desktop.ui.gui.action;

import java.awt.FileDialog;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;

import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Base58;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.common.utils.Mnemonics;
import io.zdp.common.utils.Mnemonics.Language;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;
import io.zdp.wallet.desktop.ui.common.Alert;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.common.TextComponentFocuser;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.WalletRestorationPanel;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class RestoreWallet {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	public void restore(Window parent) {

		WalletRestorationPanel panel = new WalletRestorationPanel();

		new QTextComponentContextMenu(panel.txtListOfWords);
		new QTextComponentContextMenu(panel.txtPrivateKey);

		panel.txtListOfWords.addFocusListener(new TextComponentFocuser());
		panel.txtPrivateKey.addFocusListener(new TextComponentFocuser());

		JDialog dialog = SwingHelper.dialog(parent, panel);
		dialog.setTitle("Restore wallet");

		panel.btnRestoreWallet.addActionListener(ev -> {

			// validate private key or list of words
			String privateKey = "";

			if (StringUtils.isNotBlank(panel.txtPrivateKey.getText())) {

				privateKey = panel.txtPrivateKey.getText().trim();

			} else if (StringUtils.isNotBlank(panel.txtListOfWords.getText())) {

				//String words = panel.txtPrivateKey.getText().split("");
				String[] split = StringUtils.split(panel.txtListOfWords.getText().trim(), "\r\n, ");
				List<String> words = Arrays.asList(split);

				privateKey = Base58.encode( Mnemonics.generateSeedFromWords(Language.valueOf(panel.language.getSelectedItem().toString().toUpperCase()), words) );

			} else {

				Alert.warn("Please, enter a private key or a list of words to restore a wallet");
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

			dialog.dispose();

			log.debug("Save new wallet: " + walletFile);

			try {

				Wallet w = WalletService.create(privateKey, walletFile);

				walletService.setCurrentWallet(w, walletFile);

				mainWindow.setWallet(w, walletFile);

				if (parent != mainWindow.getFrame()) {
					parent.dispose();
				}

				Alert.info("New wallet was created!");

			} catch (Exception e) {
				log.error("Error: ", e);
			}
		});

		panel.btnCancel.addActionListener(e -> {
			dialog.dispose();
		});

		SwingHelper.installEscapeCloseOperation(dialog);

		dialog.setVisible(true);
	}

}
