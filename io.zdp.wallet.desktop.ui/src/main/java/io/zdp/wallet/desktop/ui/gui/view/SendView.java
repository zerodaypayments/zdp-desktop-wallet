package io.zdp.wallet.desktop.ui.gui.view;

import java.awt.TrayIcon.MessageType;
import java.math.BigDecimal;
import java.security.PrivateKey;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
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

import io.zdp.api.model.BigDecimalValue;
import io.zdp.api.model.TransferResponse;
import io.zdp.client.ZdpClient;
import io.zdp.common.crypto.CryptoUtils;
import io.zdp.common.crypto.Signer;
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

	public JPanel get() {

		final SendPanel sendPanel = new SendPanel();

		BigDecimalValue feeHolder = new BigDecimalValue();
		BigDecimalValue addressBalanceHolder = new BigDecimalValue();

		ImageIcon icon = new ImageIcon(this.getClass().getResource("/icons/ajax-loader.gif"));
		sendPanel.btnSendMax.setIcon(icon);
		sendPanel.btnSendMax.setText("Getting fee from the network...");
		sendPanel.btnSendMax.addActionListener(e -> {

			log.debug("Send maximum");

			if (addressBalanceHolder.getValue().compareTo(feeHolder.getValue()) > 0) {
				BigDecimal max = addressBalanceHolder.getValue().subtract(feeHolder.getValue());
				sendPanel.txtAmount.setText(max.toString());
			}

		});

		new Thread(() -> {
			try {
				BigDecimal f = zdp.getFee();
				log.debug("Got fee: " + f);
				feeHolder.setValue(f);

				SwingUtilities.invokeLater(() -> {
					sendPanel.btnSendMax.setIcon(Icons.getIcon("check.png"));
					sendPanel.btnSendMax.setText("Send max");
					sendPanel.txtFee.setText(feeHolder.getValue().toString());

				});

			} catch (Exception e1) {
				log.error("Error: ", e1);
			}
		}).start();

		DefaultComboBoxModel<String> addressModel = new DefaultComboBoxModel<>();

		sendPanel.selectorFromAddress.setModel(addressModel);

		sendPanel.selectorFromAddress.addItemListener(e -> {
			String selectedAddress = sendPanel.selectorFromAddress.getSelectedItem().toString();
			WalletAddress addr = walletService.getCurrentWallet().getByPublicKeyHash(selectedAddress);
			addressBalanceHolder.setValue(addr.getBalance());
			sendPanel.txtAddressBalance.setText(addr.getBalance().toString());
		});

		for (WalletAddress addr : walletService.getCurrentWallet().getAddresses()) {
			addressModel.addElement(WalletService.getPublicKeyHash(addr));
		}

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

		JScrollPane scroll = new JScrollPane(sendPanel);
		SwingHelper.updateScrollPane(scroll);

		sendPanel.btnClear.addActionListener(e -> {
			sendPanel.txtToAddress.setText(StringUtils.EMPTY);
			sendPanel.txtAmount.setText(StringUtils.EMPTY);
			sendPanel.txtSenderRef.setText(StringUtils.EMPTY);
			sendPanel.txtRecepientRef.setText(StringUtils.EMPTY);
		});

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

					log.debug("TODO actual transfer");
					WalletAddress fromAddress = walletService.getCurrentWallet().getByPublicKeyHash(panel.txtFrom.getText());
					PrivateKey privKey = Signer.generatePrivateKey(fromAddress.getPrivateKey());
					BigDecimal amountToSend = new BigDecimal(panel.txtAmount.getText().trim());

					String fromAddressHash = WalletService.getPublicKeyHash(fromAddress);

					TransferResponse transferResponse = zdp.transfer(privKey, fromAddressHash, panel.txtTo.getText(), amountToSend, panel.txtSenderRef.getText(), panel.txtRecepientRef.getText());

					if (transferResponse == null || false == transferResponse.isSubmitted()) {
						mainWindow.showSystemTrayMessage(MessageType.ERROR, "Ttransaction failed " + transferResponse == null ? "" : transferResponse.getError());
					} else {
						mainWindow.showSystemTrayMessage(MessageType.INFO, "Submitted transaction " + transferResponse.getUuid());
					}

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

}
