package io.zdp.wallet.desktop.ui.gui.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.HomePanelWithEmptyAddressBook;
import io.zdp.wallet.desktop.ui.gui.dialog.WalletInfoPanel;
import io.zdp.wallet.desktop.ui.service.AddressService;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;
import io.zdp.wallet.desktop.ui.service.VelocityHelper;

@Component
public class HomeView {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	@Autowired
	private VelocityHelper velocity;

	@Value("${app.url.online.faq}")
	private String appUrlOnlineFaq;

	@Value("${app.url.online.help}")
	private String appUrlOnlineHelp;

	@Autowired
	private AddressService addressService;

	public JPanel get() {

		JPanel panel = new JPanel(new BorderLayout());

		if (this.walletService.getCurrentWallet().getAddresses().isEmpty() == false) {
			
			WalletInfoPanel walletInfoPanel = new WalletInfoPanel();
//			new QTextComponentContextMenu(walletInfoPanel.txtWalletName);

//			walletInfoPanel.txtWalletName.addFocusListener(new TextComponentFocuser());
//			walletInfoPanel.txtWalletName.setText(walletService.getCurrentWallet().getName());

			// walletInfoPanel.txtWalletDate.setText(walletService.getCurrentWallet().getDateAsString());
			// walletInfoPanel.txtWalletDate.addFocusListener(new
			// TextComponentFocuser());
			// new QTextComponentContextMenu(walletInfoPanel.txtWalletDate);

			// Total wallet balance
//			BigDecimal totalWalletBalance = 0;
//			for (WalletAddress addr : this.walletService.getCurrentWallet().getAddresses()) {
//				totalWalletBalance = totalWalletBalance.add(addr.getBalance());
//			}
//
//			panel.add(walletInfoPanel, BorderLayout.CENTER);

		} else {

			HomePanelWithEmptyAddressBook homePanel = new HomePanelWithEmptyAddressBook();

			homePanel.btnFAQ.addActionListener(e -> {
				SwingHelper.browseToUrl(appUrlOnlineFaq);
			});

			homePanel.btnOnlineHelp.addActionListener(e -> {
				SwingHelper.browseToUrl(appUrlOnlineHelp);
			});

			homePanel.btnGenerateNewAddress.addActionListener(e -> {
				addressService.generateNewAddress();
			});

			panel.add(new JScrollPane(homePanel), BorderLayout.CENTER);

		}
		
		return panel;

	}

}
