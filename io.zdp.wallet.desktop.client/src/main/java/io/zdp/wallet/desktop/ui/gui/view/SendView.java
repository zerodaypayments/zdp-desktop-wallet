package io.zdp.wallet.desktop.ui.gui.view;

import java.awt.TrayIcon.MessageType;
import java.math.BigDecimal;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.api.model.v1.GetBalanceResponse;
import io.zdp.api.model.v1.GetFeeResponse;
import io.zdp.api.model.v1.TransferResponse;
import io.zdp.client.ZdpClient;
import io.zdp.crypto.Curves;
import io.zdp.crypto.account.ZDPAccountUuid;
import io.zdp.wallet.api.db.domain.Account;
import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.desktop.ui.common.Alert;
import io.zdp.wallet.desktop.ui.common.Icons;
import io.zdp.wallet.desktop.ui.common.JTextFieldLimit;
import io.zdp.wallet.desktop.ui.common.NumberHelper;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.common.TextComponentFocuser;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.SendPanel;
import io.zdp.wallet.desktop.ui.gui.dialog.TransferConfirmationPanel;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class SendView {

	private final Logger log = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private ZdpClient zdp;

	@Autowired
	private MainWindow mainWindow;

	private Icon loadingIcon = Icons.getIcon( "ajax-loader.gif" );

	public JPanel get ( ) {

		final SendPanel sendPanel = new SendPanel();

		GetFeeResponse fee = new GetFeeResponse();

		sendPanel.btnSendMax.setEnabled( false );
		sendPanel.btnSendMax.addActionListener( e -> {

			log.debug( "Send maximum" );

			Wallet w = walletService.getCurrentWallet();
			Account acc = w.getAccounts().get( 0 );

			BigDecimal max = acc.getBalance().subtract( new BigDecimal( fee.getFee() ) );
			sendPanel.txtAmount.setText( max.toPlainString() );

		} );

		sendPanel.txtAddressBalance.setIcon( loadingIcon );
		sendPanel.txtFee.setIcon( loadingIcon );

		try {

			final String publicKey58 = walletService.getCurrentWallet().getAccounts().get( 0 ).getZdpUuid();

			sendPanel.txtFromAddress.setText( publicKey58 );
			sendPanel.txtFromAddress.setCaretPosition( 0 );
		} catch ( Exception e2 ) {
			log.error( "Error: ", e2 );
		}

		new Thread( ( ) -> {
			try {
				fee.setFee( zdp.getFee().getFee() );
				log.debug( "Got fee: " + fee.getFee() );
				sendPanel.btnSendMax.setEnabled( true );
				SwingUtilities.invokeLater( ( ) -> {
					sendPanel.txtFee.setText( fee.getFee() );
					sendPanel.txtFee.setIcon( Icons.getIcon( "check.png" ) );
				} );

			} catch ( Exception e1 ) {
				log.error( "Error: ", e1 );
			}
		} ).start();

		updateAddressBalance( sendPanel );

		new QTextComponentContextMenu( sendPanel.txtToAddress );
		sendPanel.txtToAddress.addFocusListener( new TextComponentFocuser() );

		new QTextComponentContextMenu( sendPanel.txtAmount );
		sendPanel.txtAmount.setDocument( new JTextFieldLimit( 32 ) );
		sendPanel.txtAmount.addFocusListener( new TextComponentFocuser() );

		new QTextComponentContextMenu( sendPanel.txtMemo );
		sendPanel.txtMemo.setDocument( new JTextFieldLimit( 64 ) );
		sendPanel.txtMemo.addFocusListener( new TextComponentFocuser() );

		new QTextComponentContextMenu( sendPanel.txtFromAddress );
		sendPanel.txtFromAddress.addFocusListener( new TextComponentFocuser() );

		new QTextComponentContextMenu( sendPanel.txtTotalCharge );

		JScrollPane scroll = new JScrollPane( sendPanel );
		SwingHelper.updateScrollPane( scroll );

		sendPanel.txtAmount.getDocument().addDocumentListener( new DocumentListener() {

			@Override
			public void removeUpdate ( DocumentEvent e ) {
				updateTotalCharge();
			}

			@Override
			public void insertUpdate ( DocumentEvent e ) {
				updateTotalCharge();
			}

			@Override
			public void changedUpdate ( DocumentEvent e ) {
				updateTotalCharge();
			}

			void updateTotalCharge ( ) {
				String amountText = sendPanel.txtAmount.getText();
				if ( NumberUtils.isCreatable( amountText ) && NumberUtils.isParsable( amountText ) ) {
					BigDecimal amount = new BigDecimal( amountText );
					BigDecimal total = amount.add( new BigDecimal( fee.getFee() ) );
					sendPanel.txtTotalCharge.setText( total.toString() );
				}
			}

		} );

		sendPanel.btnSend.addActionListener( e -> {

			log.debug( "Send funds" );

			String from = sendPanel.txtFromAddress.getText();
			String to = sendPanel.txtToAddress.getText();

			if ( false == NumberUtils.isParsable( sendPanel.txtAmount.getText().trim() ) ) {
				Alert.warn( "Please, enter a valid amount" );
				return;
			}

			BigDecimal amount = new BigDecimal( sendPanel.txtAmount.getText().trim() );

			if ( amount.compareTo( BigDecimal.ZERO ) < 0 ) {
				Alert.warn( "Please, enter a valid amount" );
				return;
			}

			if ( ZDPAccountUuid.isValidUuid( to ) == false ) {
				Alert.warn( "To address is not valid" );
				return;
			}

			Wallet w = walletService.getCurrentWallet();

			Account currentAccount = w.getAccounts().get( 0 );

			if ( amount.compareTo( currentAccount.getBalance() ) > 0 ) {
				Alert.warn( "This address only has " + currentAccount.getBalance() );
				return;
			}

			log.debug( "Send from " + from + " to " + to + ", amount: " + amount );

			TransferConfirmationPanel panel = new TransferConfirmationPanel();
			panel.txtAmount.setText( sendPanel.txtAmount.getText() );
			panel.txtFrom.setText( sendPanel.txtFromAddress.getText() );
			panel.txtTo.setText( sendPanel.txtToAddress.getText() );
			panel.txtMemo.setText( sendPanel.txtMemo.getText() );

			new QTextComponentContextMenu( panel.txtAmount );
			new QTextComponentContextMenu( panel.txtFrom );
			new QTextComponentContextMenu( panel.txtTo );
			new QTextComponentContextMenu( panel.txtMemo );

			panel.txtFrom.setCaretPosition( 0 );
			panel.txtTo.setCaretPosition( 0 );

			JDialog dialog = SwingHelper.dialog( mainWindow.getFrame(), panel );

			panel.btnCancel.addActionListener( ev -> {
				dialog.dispose();
			} );

			panel.btnTransfer.addActionListener( ev -> {

				try {

					BigDecimal amountToSend = new BigDecimal( panel.txtAmount.getText().trim() );

					TransferResponse transferResponse = zdp.transfer( currentAccount.getPrivateKey(), Curves.DEFAULT_CURVE, panel.txtFrom.getText(), panel.txtTo.getText(), amountToSend, panel.txtMemo.getText() );

					if ( transferResponse == null || StringUtils.isBlank( transferResponse.getUuid() ) ) {

						String err = "Transaction failed " + transferResponse == null ? "" : transferResponse.getError();
						mainWindow.showSystemTrayMessage( MessageType.ERROR, err );
						Alert.error( err );

					} else {

						String msg = "Submitted transaction " + transferResponse.getUuid();
						mainWindow.showSystemTrayMessage( MessageType.INFO, msg );

						Alert.info( "Transaction submitted: " + transferResponse.getUuid() );

						updateAddressBalance( sendPanel );

						mainWindow.updateUI();

					}

					dialog.dispose();

				} catch ( Exception ex ) {
					log.error( "Error: ", ex );
				}
			} );

			SwingHelper.installEscapeCloseOperation( dialog );
			dialog.setTitle( "Confirm transfer" );
			dialog.setVisible( true );

		} );

		return sendPanel;

	}

	private void updateAddressBalance ( SendPanel sendPanel ) {

		sendPanel.txtAddressBalance.setIcon( loadingIcon );
		sendPanel.txtAddressBalance.setText( "Getting account balance" );

		new Thread( ( ) -> {

			try {

				Wallet w = walletService.getCurrentWallet();
				Account acc = w.getAccounts().get( 0 );

				GetBalanceResponse resp = zdp.getBalance( acc.getPrivateKey(), Curves.DEFAULT_CURVE );
				walletService.saveAccountDetails( acc, resp );

				sendPanel.txtAddressBalance.setText( NumberHelper.toGroupedAmount( acc.getBalance() ) );

				sendPanel.txtAddressBalance.setIcon( Icons.getIcon( "check.png" ) );

			} catch ( Exception e ) {
				log.error( "Error: ", e );
			}

		} ).start();

	}

}
