package io.zdp.wallet.desktop.ui.gui.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.event.HyperlinkEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.common.utils.Mnemonics;
import io.zdp.common.utils.Mnemonics.Language;
import io.zdp.wallet.api.domain.WalletAddress;
import io.zdp.wallet.api.service.WalletService;
import io.zdp.wallet.desktop.ui.common.Icons;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.common.TextComponentFocuser;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.MyAddressesPanel;
import io.zdp.wallet.desktop.ui.gui.dialog.RecepientsAddressesPanel;
import io.zdp.wallet.desktop.ui.gui.dialog.ShowAddressSecretKeyPanel;
import io.zdp.wallet.desktop.ui.service.AddressService;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;
import io.zdp.wallet.desktop.ui.service.VelocityHelper;

@Component
public class AddressBookView {

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	@Autowired
	private AddressService addressService;

	@Autowired
	private VelocityHelper velocity;

	public JTabbedPane get() {

		JTabbedPane tabs = new JTabbedPane();

		// My addresses
		MyAddressesPanel myAddressesPanel = new MyAddressesPanel();
		new QTextComponentContextMenu(myAddressesPanel.html);
		SwingHelper.setFontForJText(myAddressesPanel.html);

		// Populate my addresses from wallet
		if (walletService.getCurrentWallet().getAddresses().isEmpty() == false) {

			Map<String, Object> events = new HashMap<>();
			events.put("addresses", walletService.getCurrentWallet().getAddresses());
			String html = velocity.process(events, "/html/myAddresses.html");

			SwingHelper.setFontForJText(myAddressesPanel.html);
			myAddressesPanel.html.setText(html);

			myAddressesPanel.html.addHyperlinkListener(e -> {

				System.out.println(e.getDescription());

				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

					if (e.getDescription().startsWith("secret:")) {

						ShowAddressSecretKeyPanel panel = new ShowAddressSecretKeyPanel();

						JDialog dialog = SwingHelper.dialog(mainWindow.getFrame(), panel);
						SwingHelper.installEscapeCloseOperation(dialog);
						dialog.setModal(true);
						dialog.pack();
						panel.btnClose.addActionListener(ev -> {
							dialog.dispose();
						});

						WalletAddress addr = walletService.getCurrentWallet().getByPublicKeyHash(e.getDescription().substring("secret:".length()));

						dialog.setTitle("Secret key for address " + WalletService.getPublicKeyHash(addr));

						panel.txtSecretKey.setText(addr.getSeed());
						new QTextComponentContextMenu(panel.txtSecretKey);
						panel.txtSecretKey.addFocusListener(new TextComponentFocuser());

						// Mnemonics
						mnemonics(panel, addr);

						panel.languageSelector.addItemListener(i -> {
							mnemonics(panel, addr);
						});

						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);

					}

					/*
					 * renameAddress(()->{ showAddressBook(); });
					 */

				}
			});
		}

		tabs.addTab("My addresses", Icons.getIcon("wallet_32.png"), myAddressesPanel, "My addresses and balances");

		myAddressesPanel.btnGenerateNewAddress.addActionListener(e -> {
			addressService.generateNewAddress();
		});

		// Recepients addresses
		RecepientsAddressesPanel recepientsAddressPanel = new RecepientsAddressesPanel();
		tabs.addTab("Recepients' addresses", Icons.getIcon("address_book_32.png"), recepientsAddressPanel);

		new QTextComponentContextMenu(recepientsAddressPanel.html);
		SwingHelper.setFontForJText(recepientsAddressPanel.html);

		return tabs;
	}

	private void mnemonics(ShowAddressSecretKeyPanel panel, WalletAddress addr) {
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

		List<String> words = Mnemonics.generateWords(l, addr.getSeed());

		int index = 0;
		for (int column = 0; column < 3; column++) {
			for (int row = 0; row < 8; row++) {
				panel.tableMnemonics.getModel().setValueAt(words.get(index), row, column);
				index++;
			}
		}

	}
}
