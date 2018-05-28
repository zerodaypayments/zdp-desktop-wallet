package io.zdp.wallet.desktop.ui.gui.action;

import java.awt.Window;
import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import javax.swing.JDialog;

import org.apache.commons.lang3.StringUtils;
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
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.common.SynchronousJFXFileChooser;
import io.zdp.wallet.desktop.ui.common.TextComponentFocuser;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.WalletRestorationPanel;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;
import javafx.application.Platform;
import javafx.stage.FileChooser;

@Component
public class RestoreWallet {

	private final Logger log = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	public void restore ( Window parent ) {

		WalletRestorationPanel panel = new WalletRestorationPanel();

		new QTextComponentContextMenu( panel.txtListOfWords );
		new QTextComponentContextMenu( panel.txtPrivateKey );

		panel.txtListOfWords.addFocusListener( new TextComponentFocuser() );
		panel.txtPrivateKey.addFocusListener( new TextComponentFocuser() );

		JDialog dialog = SwingHelper.dialog( parent, panel );
		dialog.setTitle( "Restore wallet" );

		panel.btnRestoreWallet.addActionListener( ev -> {

			// validate private key or list of words
			String privateKey = "";

			if ( StringUtils.isNotBlank( panel.txtPrivateKey.getText() ) ) {

				privateKey = panel.txtPrivateKey.getText().trim();

			} else if ( StringUtils.isNotBlank( panel.txtListOfWords.getText() ) ) {

				//String words = panel.txtPrivateKey.getText().split("");
				String [ ] split = StringUtils.split( panel.txtListOfWords.getText().trim(), "\r\n, " );
				List < String > words = Arrays.asList( split );

				BigInteger bi = Mnemonics.generatePrivateKeyFromWords( Language.valueOf( panel.language.getSelectedItem().toString().toUpperCase() ), words );

				privateKey = ZDPKeyPair.createFromPrivateKeyBigInteger( bi, Curves.DEFAULT_CURVE ).getPrivateKeyAsBase58();

			} else {

				Alert.warn( "Please, enter a private key or a list of words to restore a wallet" );
				return;

			}

			javafx.embed.swing.JFXPanel dummy = new javafx.embed.swing.JFXPanel();
			Platform.setImplicitExit( false );

			SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser( ( ) -> {
				FileChooser ch = new FileChooser();

				ch.setTitle( "Save wallet file" );

				return ch;
			} );

			File walletFile = chooser.showSaveDialog();

			if ( walletFile == null ) {
				return;
			}

			// Enter DB file password for AES encryption
			String password = CreateNewWallet.enterPassword( parent );

			// Looks like the operation was cancelled
			if ( password == null ) {
				log.info( "No password entered, cancel" );
				return;
			}

			dialog.dispose();

			log.debug( "Save new wallet: " + walletFile );

			try {

				ZDPKeyPair kp = ZDPKeyPair.createFromPrivateKeyBase58( privateKey, Curves.DEFAULT_CURVE );

				Wallet w = walletService.create( password, walletFile, kp );

				walletService.setCurrentWallet( w, walletFile );

				mainWindow.setWallet( w, walletFile );

				if ( parent != mainWindow.getFrame() ) {
					parent.dispose();
				}

				Alert.info( "New wallet was created!" );

			} catch ( Exception e ) {
				log.error( "Error: ", e );
			}
		} );

		panel.btnCancel.addActionListener( e -> {
			dialog.dispose();
		} );

		SwingHelper.installEscapeCloseOperation( dialog );

		dialog.setVisible( true );
	}

}
