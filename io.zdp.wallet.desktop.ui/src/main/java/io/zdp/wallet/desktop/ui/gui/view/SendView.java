package io.zdp.wallet.desktop.ui.gui.view;

import java.awt.TrayIcon.MessageType;
import java.math.BigDecimal;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.api.model.BalanceResponse;
import io.zdp.api.model.BigDecimalValue;
import io.zdp.api.model.TransferResponse;
import io.zdp.client.ZdpClient;
import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.api.domain.WalletAddress;
import io.zdp.wallet.api.service.WalletService;
import io.zdp.wallet.desktop.ui.common.Alert;
import io.zdp.wallet.desktop.ui.common.Icons;
import io.zdp.wallet.desktop.ui.common.JTextFieldLimit;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.common.TextComponentFocuser;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.SendPanel;
import io.zdp.wallet.desktop.ui.gui.dialog.TransferConfirmationPanel;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class SendView {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private ZdpClient zdp;

	@Autowired
	private MainWindow mainWindow;

	private Icon loadingIcon = Icons.getIcon("ajax-loader.gif");

	public JPanel get() {

		final SendPanel sendPanel = new SendPanel();

		BigDecimalValue feeHolder = new BigDecimalValue();

		/*
		sendPanel.btnSendMax.setIcon(icon);
		sendPanel.btnSendMax.setText("Getting fee from the network...");
		sendPanel.btnSendMax.addActionListener(e -> {
		
			log.debug("Send maximum");
		
			if (addressBalanceHolder.getValue().compareTo(feeHolder.getValue()) > 0) {
				BigDecimal max = addressBalanceHolder.getValue().subtract(feeHolder.getValue());
				sendPanel.txtAmount.setText(max.toString());
			}
		
		});
		*/

		sendPanel.txtAddressBalance.setIcon(loadingIcon);
		sendPanel.txtFee.setIcon(loadingIcon);

		new Thread(() -> {
			try {
				BigDecimal f = zdp.getFee();
				log.debug("Got fee: " + f);
				feeHolder.setValue(f);

				SwingUtilities.invokeLater(() -> {
					sendPanel.txtFee.setText(feeHolder.getValue().toString());
					sendPanel.txtFee.setIcon(Icons.getIcon("check.png"));
				});

			} catch (Exception e1) {
				log.error("Error: ", e1);
			}
		}).start();

		DefaultComboBoxModel<String> addressModel = new DefaultComboBoxModel<>();

		sendPanel.selectorFromAddress.setModel(addressModel);

		sendPanel.selectorFromAddress.addItemListener(e -> {
			updateAddressBalance(sendPanel);
		});

		for (WalletAddress addr : walletService.getCurrentWallet().getAddresses()) {
			addressModel.addElement(WalletService.getPublicKeyHash(addr));
		}

		updateAddressBalance(sendPanel);

		new QTextComponentContextMenu(sendPanel.txtToAddress);
		sendPanel.txtToAddress.addFocusListener(new TextComponentFocuser());
		sendPanel.txtToAddress.setDocument(new JTextFieldLimit(44));

		new QTextComponentContextMenu(sendPanel.txtAmount);
		sendPanel.txtAmount.setDocument(new JTextFieldLimit(32));
		sendPanel.txtAmount.addFocusListener(new TextComponentFocuser());

		new QTextComponentContextMenu(sendPanel.txtSenderRef);
		sendPanel.txtSenderRef.setDocument(new JTextFieldLimit(64));
		sendPanel.txtSenderRef.addFocusListener(new TextComponentFocuser());

		new QTextComponentContextMenu(sendPanel.txtRecepientRef);
		sendPanel.txtRecepientRef.setDocument(new JTextFieldLimit(64));
		sendPanel.txtRecepientRef.addFocusListener(new TextComponentFocuser());

		new QTextComponentContextMenu(sendPanel.txtTotalCharge);

		JScrollPane scroll = new JScrollPane(sendPanel);
		SwingHelper.updateScrollPane(scroll);

		sendPanel.txtAmount.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateTotalCharge();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateTotalCharge();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateTotalCharge();
			}

			void updateTotalCharge() {
				String amountText = sendPanel.txtAmount.getText();
				if (NumberUtils.isCreatable(amountText) && NumberUtils.isParsable(amountText)) {
					BigDecimal amount = new BigDecimal(amountText);
					BigDecimal total = amount.add(feeHolder.getValue());
					sendPanel.txtTotalCharge.setText(total.toString());
				}
			}

		});

		sendPanel.btnSend.addActionListener(e -> {

			log.debug("Send funds");

			String from = sendPanel.selectorFromAddress.getSelectedItem().toString();
			String to = sendPanel.txtToAddress.getText();

			if (false == NumberUtils.isParsable(sendPanel.txtAmount.getText().trim())) {
				Alert.warn("Please, enter a valid amount");
				return;
			}
			
			BigDecimal amount = new BigDecimal(sendPanel.txtAmount.getText().trim());

			if (amount.compareTo(BigDecimal.ZERO) < 0) {
				Alert.warn("Please, enter a valid amount");
				return;
			}

			if (CryptoUtils.isValidAddress(to) == false) {
				Alert.warn("To address is not valid");
				return;
			}

			WalletAddress addr = walletService.getCurrentWallet().getByPublicKeyHash(from);

			if (amount.compareTo(addr.getBalance()) > 0) {
				Alert.warn("This address only has " + addr.getBalance());
				return;
			}

			log.debug("Send from " + from + " to " + to + ", amount: " + amount);

			TransferConfirmationPanel panel = new TransferConfirmationPanel();
			panel.txtAmount.setText(sendPanel.txtAmount.getText());
			panel.txtFrom.setText(sendPanel.selectorFromAddress.getSelectedItem().toString());
			panel.txtTo.setText(sendPanel.txtToAddress.getText());
			panel.txtSenderRef.setText(sendPanel.txtSenderRef.getText());
			panel.txtRecepientRef.setText(sendPanel.txtRecepientRef.getText());

			new QTextComponentContextMenu(panel.txtAmount);
			new QTextComponentContextMenu(panel.txtFrom);
			new QTextComponentContextMenu(panel.txtTo);
			new QTextComponentContextMenu(panel.txtSenderRef);
			new QTextComponentContextMenu(panel.txtRecepientRef);

			JDialog dialog = SwingHelper.dialog(mainWindow.getFrame(), panel);

			panel.btnCancel.addActionListener(ev -> {
				dialog.dispose();
			});

			panel.btnTransfer.addActionListener(ev -> {

				try {

					WalletAddress fromAddress = walletService.getCurrentWallet().getByPublicKeyHash(panel.txtFrom.getText());

					BigDecimal amountToSend = new BigDecimal(panel.txtAmount.getText().trim());

					TransferResponse transferResponse = zdp.transfer(fromAddress.getPublicKey(), fromAddress.getPrivateKey(), panel.txtTo.getText(), amountToSend, panel.txtSenderRef.getText(), panel.txtRecepientRef.getText());

					if (transferResponse == null || false == transferResponse.isSubmitted()) {
						
						String err = "Transaction failed " + transferResponse == null ? "" : transferResponse.getError();
						mainWindow.showSystemTrayMessage(MessageType.ERROR, err);
						Alert.error(err);
						
					} else {
						
						
						String msg = "Submitted transaction " + transferResponse.getUuid();
						mainWindow.showSystemTrayMessage(MessageType.INFO, msg);
						
						Alert.info("Transaction submitted: " + transferResponse.getUuid());
						
						updateAddressBalance(sendPanel);
					}
					
					dialog.dispose();
					

				} catch (Exception ex) {
					log.error("Error: ", ex);
				}
			});

			SwingHelper.installEscapeCloseOperation(dialog);
			dialog.setTitle("Confirm transfer");
			dialog.setVisible(true);

		});

		return sendPanel;

	}

	private void updateAddressBalance(SendPanel sendPanel) {

		sendPanel.txtAddressBalance.setIcon(loadingIcon);
		sendPanel.txtAddressBalance.setText("Getting address balance");

		String selectedAddress = sendPanel.selectorFromAddress.getSelectedItem().toString();

		WalletAddress addr = walletService.getCurrentWallet().getByPublicKeyHash(selectedAddress);

		new Thread(() -> {

			try {

				BalanceResponse balance = zdp.getAddressBalance(addr.getPublicKey(), addr.getPrivateKey());
				sendPanel.txtAddressBalance.setText(balance.getBalance().toString());
				sendPanel.txtAddressBalance.setIcon(Icons.getIcon("check.png"));

				addr.setBalance(balance.getBalance());

			} catch (Exception e) {
				log.error("Error: ", e);
			}

		}).start();

	}

}
