package io.zdp.wallet.desktop.ui.gui.action;

import java.awt.Window;
import java.io.File;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.crypto.Curves;
import io.zdp.crypto.key.ZDPKeyPair;
import io.zdp.crypto.mnemonics.Mnemonics;
import io.zdp.crypto.mnemonics.Mnemonics.Language;
import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.desktop.ui.common.Alert;
import io.zdp.wallet.desktop.ui.common.I18n;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.common.SynchronousJFXFileChooser;
import io.zdp.wallet.desktop.ui.common.TextComponentFocuser;
import io.zdp.wallet.desktop.ui.common.model.BooleanWrapper;
import io.zdp.wallet.desktop.ui.common.model.StringWrapper;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.PasswordPanel;
import io.zdp.wallet.desktop.ui.gui.dialog.WalletCreationPanel;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;
import javafx.application.Platform;
import javafx.stage.FileChooser;

@Component
public class CreateNewWallet {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private I18n i18n;

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	public void create( Window parent ) {

		// Enter DB file password for AES encryption
		String password = enterPassword(parent);

		// Looks like the operation was cancelled
		if (password == null) {
			log.info("No password entered, cancel");
			return;
		}

		// File save dialog
		javafx.embed.swing.JFXPanel dummy = new javafx.embed.swing.JFXPanel();
		Platform.setImplicitExit(false);

		SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
			FileChooser ch = new FileChooser();

			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ZDP Wallet (*.zdp)", "*.zdp");
			ch.getExtensionFilters().add(extFilter);

			ch.setTitle("Save wallet file");

			return ch;
		});

		File walletFile = chooser.showOpenDialog();

		if (walletFile == null || false == walletFile.canWrite()) {
			return;
		}
/*
		newWalletDialog.dispose();

		log.debug("Save new wallet: " + walletFile);

		try {

			String seed = panel.txtSeed.getText();

			Wallet w = walletService.create(seed, walletFile);

			walletService.setCurrentWallet(w, walletFile);

			mainWindow.setWallet(w, walletFile);

			if (parent != mainWindow.getFrame()) {
				parent.dispose();
			}

			Alert.info("New wallet was created!");

		} catch (Exception e) {
			log.error("Error: ", e);
		}
*/
	}

	private String enterPassword( Window parent ) {

		StringWrapper password = new StringWrapper();

		final BooleanWrapper cancelled = new BooleanWrapper(false);

		while (password.get() == null && cancelled.isFalse()) {

			PasswordPanel pp = new PasswordPanel();

			JDialog eppDialog = SwingHelper.dialog(parent, pp);
			SwingHelper.installEscapeCloseOperation(eppDialog, cancelled);

			pp.btnOk.setEnabled(true);

			pp.btnOk.addActionListener(e -> {
				
				
				if (pp.password.getPassword().length == 0 || pp.passwordConfirm.getPassword().length == 0) {
					
					JOptionPane.showMessageDialog(parent, "Please, enter passwords", "Warning", JOptionPane.WARNING_MESSAGE);
					
				} else if (pp.password.getPassword().length > 0 && Arrays.areEqual(pp.password.getPassword(), pp.passwordConfirm.getPassword())) {

					password.set(new String(pp.password.getPassword()));

					eppDialog.dispose();

				} else {

					JOptionPane.showMessageDialog(parent, "The passwords do not match", "Warning", JOptionPane.WARNING_MESSAGE);

				}

			});

			pp.btnCancel.addActionListener(e -> {
				cancelled.set(true);
				eppDialog.dispose();
			});

			eppDialog.setVisible(true);

		}

		return password.get();

	}

}
