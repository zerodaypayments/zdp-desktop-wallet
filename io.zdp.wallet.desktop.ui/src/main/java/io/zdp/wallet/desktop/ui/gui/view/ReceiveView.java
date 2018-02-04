package io.zdp.wallet.desktop.ui.gui.view;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.ReceivePanel;
import io.zdp.wallet.desktop.ui.service.AddressService;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;
import io.zdp.wallet.desktop.ui.service.VelocityHelper;

@Component
public class ReceiveView {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AddressService addressService;

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	@Autowired
	private VelocityHelper velocity;

	public JPanel get() {

		ReceivePanel panel = new ReceivePanel();

		SwingHelper.setFontForJText(panel.html);

		new QTextComponentContextMenu(panel.html);

		if (this.walletService.getCurrentWallet().getAddresses().isEmpty()) {

			panel.html.setText("There are no addresses in this wallet yet");

		} else {

			Map<String, Object> events = new HashMap<>();
			events.put("addresses", walletService.getCurrentWallet().getAddresses());
			String html = velocity.process(events, "/html/receive.html");

			panel.html.setText(html);
		}

		panel.btnNewAddress.addActionListener(e -> {
			addressService.generateNewAddress();

		});

		return panel;

	}

}
