package io.zdp.wallet.desktop.ui.gui.view;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.zdp.common.utils.StringHelper;
import io.zdp.wallet.desktop.api.domain.MyAddress;
import io.zdp.wallet.desktop.ui.common.Alert;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.common.TextComponentFocuser;
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

		if (this.walletService.getCurrentWallet().getMyAddresses().isEmpty() == false) {

			WalletInfoPanel walletInfoPanel = new WalletInfoPanel();
			new QTextComponentContextMenu(walletInfoPanel.txtWalletName);

			walletInfoPanel.txtWalletName.addFocusListener(new TextComponentFocuser());
			walletInfoPanel.txtWalletName.setText(walletService.getCurrentWallet().getName());

			// walletInfoPanel.txtWalletDate.setText(walletService.getCurrentWallet().getDateAsString());
			// walletInfoPanel.txtWalletDate.addFocusListener(new
			// TextComponentFocuser());
			// new QTextComponentContextMenu(walletInfoPanel.txtWalletDate);

			// Total wallet balance
			double totalWalletBalance = 0;
			for (MyAddress addr : this.walletService.getCurrentWallet().getMyAddresses()) {
				totalWalletBalance += addr.getBalance();
			}

			walletInfoPanel.txtTotalWalletBalance.setText(StringHelper.format(totalWalletBalance));
			new QTextComponentContextMenu(walletInfoPanel.txtTotalWalletBalance);
			walletInfoPanel.txtTotalWalletBalance.addFocusListener(new TextComponentFocuser());

			// Recent events
			Map<String, Object> events = new HashMap<>();
			events.put("events", walletService.getCurrentWallet().getWalletEvents());
			String eventsHtml = velocity.process(events, "/html/recentEvents.html");

			SwingHelper.setFontForJText(walletInfoPanel.textRecentEvents);
			walletInfoPanel.textRecentEvents.setText(eventsHtml);
			new QTextComponentContextMenu(walletInfoPanel.textRecentEvents);

			walletInfoPanel.btnUpdateName.addActionListener(e -> {
				if (StringUtils.isNotBlank(walletInfoPanel.txtWalletName.getText())) {
					walletService.getCurrentWallet().setName(walletInfoPanel.txtWalletName.getText());
					walletService.saveCurrentWallet();
					mainWindow.updateFrame(walletService.getCurrentWallet());
					Alert.info("Wallet successfully updated!");
				}
			});

			panel.add(walletInfoPanel, BorderLayout.CENTER);

		} else {

			HomePanelWithEmptyAddressBook homePanel = new HomePanelWithEmptyAddressBook();

			homePanel.btnFAQ.addActionListener(e -> {
				SwingHelper.browseToUrl(appUrlOnlineFaq);
			});

			homePanel.btnOnlineHelp.addActionListener(e -> {
				SwingHelper.browseToUrl(appUrlOnlineHelp);
			});

			homePanel.btnGenerateNewAddress.addActionListener(e -> {

				addressService.generateNewAddress(() -> {
					mainWindow.showHomeScreen();
				});

			});

			panel.add(homePanel, BorderLayout.CENTER);

		}
		
		return panel;

	}

}
