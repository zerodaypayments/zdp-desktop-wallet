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
import io.zdp.wallet.desktop.ui.gui.dialog.HomePanelWithEmptyAddressBook;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class HomeView {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DesktopWalletService walletService;

	@Value("${app.url.online.faq}")
	private String appUrlOnlineFaq;

	@Value("${app.url.online.help}")
	private String appUrlOnlineHelp;

	public JPanel get() {

		JPanel panel = new JPanel(new BorderLayout());

		HomePanelWithEmptyAddressBook homePanel = new HomePanelWithEmptyAddressBook();

		homePanel.btnFAQ.addActionListener(e -> {
			SwingHelper.browseToUrl(appUrlOnlineFaq);
		});

		homePanel.btnOnlineHelp.addActionListener(e -> {
			SwingHelper.browseToUrl(appUrlOnlineHelp);
		});

		homePanel.txtBalance.setText(walletService.getCurrentWallet().getBalance().toString());

		panel.add(new JScrollPane(homePanel), BorderLayout.CENTER);

		return panel;

	}

}
