package io.zdp.wallet.desktop.ui.gui.action;

import java.awt.FileDialog;
import java.awt.Window;
import java.io.File;
import java.io.FilenameFilter;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.common.utils.Mnemonics;
import io.zdp.common.utils.Mnemonics.Language;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;
import io.zdp.wallet.desktop.ui.common.Alert;
import io.zdp.wallet.desktop.ui.common.I18n;
import io.zdp.wallet.desktop.ui.common.Icons;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.common.TextComponentFocuser;
import io.zdp.wallet.desktop.ui.common.model.BooleanWrapper;
import io.zdp.wallet.desktop.ui.common.model.StringWrapper;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.PasswordPanel;
import io.zdp.wallet.desktop.ui.gui.dialog.WalletCreationPanel;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class CreateNewWallet {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private I18n i18n;

	@Autowired
	private DesktopWalletService walletService;

	private Icon warningIcon = Icons.getIcon("info_32.png");

	@Autowired
	private MainWindow mainWindow;

	public void create(Window parent) {

		// Ask for a wallet password
		PasswordPanel ppanel = new PasswordPanel();

		JDialog passwordDialog = SwingHelper.dialog(mainWindow.getFrame(), ppanel);
		SwingHelper.installEscapeCloseOperation(passwordDialog);

		BooleanWrapper strongPassword = new BooleanWrapper(false);

		ppanel.password.getDocument().addDocumentListener(new DocumentListener() {

			private Zxcvbn zxcvbn = new Zxcvbn();

			@Override
			public void removeUpdate(DocumentEvent e) {
				checkPass();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				checkPass();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkPass();
			}

			private void checkPass() {

				final String password = new String(ppanel.password.getPassword());

				final Strength strength = zxcvbn.measure(password);

				if (strength.getScore() < 3) {

					String error = strength.getFeedback().getWarning();

					if (StringUtils.isNotBlank(error)) {
						ppanel.error.setText(error);
					} else {
						ppanel.error.setText("Please, use a stronger password");
					}

					ppanel.error.setIcon(warningIcon);
					strongPassword.set(false);
					
					ppanel.btnOk.setEnabled(false);

				} else {
					ppanel.error.setText("");
					ppanel.error.setIcon(null);
					strongPassword.set(true);
					ppanel.btnOk.setEnabled(true);
				}

			}
		});

		BooleanWrapper cancelled = new BooleanWrapper(false);

		ppanel.btnCancel.addActionListener(e -> {
			cancelled.set(true);
			passwordDialog.dispose();
		});

		StringWrapper passwordWrapper = new StringWrapper();

		ppanel.btnOk.addActionListener(e -> {
			
			final String password = new String(ppanel.password.getPassword());
			final String confirmed = new String(ppanel.passwordConfirm.getPassword());
			
			if (strongPassword.isFalse()) {
				Alert.warn("Please, use a stronger password!");
				return;
			}
			
			if (password.equals(confirmed)) {
				passwordWrapper.set(password);
				passwordDialog.dispose();
			} else {
				Alert.warn("Please, confirm the password");
			}
			
		});

		passwordDialog.setVisible(true);

		if (cancelled.isTrue() || strongPassword.isFalse()) {
			return;
		}

		WalletCreationPanel panel = new WalletCreationPanel();

		new QTextComponentContextMenu(panel.txtSeed);
		new QTextComponentContextMenu(panel.txtMnemonics);

		panel.txtSeed.addFocusListener(new TextComponentFocuser());
		panel.txtMnemonics.addFocusListener(new TextComponentFocuser());

		JDialog newWalletDialog = SwingHelper.dialog(parent, panel);
		newWalletDialog.setTitle("New wallet");

		generateWalletInfo(panel);

		panel.languageSelector.addItemListener(i -> {
			generateWalletInfo(panel);
		});

		panel.btnCreatWallet.addActionListener(ev -> {

			if (false == Alert.confirm("Did you write down the wallet private key or list of words?")) {
				return;
			}

			String walletPassword = passwordWrapper.get();

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

			newWalletDialog.dispose();

			log.debug("Save new wallet: " + walletFile);

			try {

				String seed = panel.txtSeed.getText();
				
				Wallet w = WalletService.create(seed, walletFile, walletPassword);

				walletService.setCurrentWallet(w, walletFile, walletPassword);

				mainWindow.setWallet(w, walletFile, walletPassword);

				if (parent != mainWindow.getFrame()) {
					parent.dispose();
				}

				Alert.info("New wallet was created!");

			} catch (Exception e) {
				log.error("Error: ", e);
			}
		});

		panel.btnCancel.addActionListener(e -> {
			newWalletDialog.dispose();
		});

		SwingHelper.installEscapeCloseOperation(newWalletDialog);

		newWalletDialog.setVisible(true);
	}

	private void generateWalletInfo(WalletCreationPanel panel) {

		try {
			String seed = CryptoUtils.generateRandomNumber256bits();
			panel.txtSeed.setText(seed);

			Language l = Language.ENGLISH;

			if (panel.languageSelector.getSelectedItem().equals("English")) {
				l = Language.ENGLISH;
			} else if (panel.languageSelector.getSelectedItem().equals("French")) {
				l = Language.FRENCH;
			} else if (panel.languageSelector.getSelectedItem().equals("Italian")) {
				l = Language.ITALIAN;
			} else if (panel.languageSelector.getSelectedItem().equals("Japanese")) {
				l = Language.JAPANESE;
			} else if (panel.languageSelector.getSelectedItem().equals("Korean")) {
				l = Language.KOREAN;
			} else if (panel.languageSelector.getSelectedItem().equals("Spanish")) {
				l = Language.SPANISH;
			} else if (panel.languageSelector.getSelectedItem().equals("Chinese Simplified")) {
				l = Language.CHINESE_SIMPLIFIED;
			} else if (panel.languageSelector.getSelectedItem().equals("Chinese Traditional")) {
				l = Language.CHINESE_TRADITIONAL;
			}

			List<String> generateWords = Mnemonics.generateWords(l, seed);
			String words = StringUtils.join(generateWords, IOUtils.LINE_SEPARATOR);
			panel.txtMnemonics.setText(words);
			panel.txtMnemonics.setCaretPosition(0);

		} catch (NoSuchAlgorithmException e) {
			log.error("Error: ", e);
		}

	}

}
