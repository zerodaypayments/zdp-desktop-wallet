package io.zdp.wallet.desktop.ui.gui.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import io.zdp.wallet.desktop.ui.service.VelocityHelper;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class ReceiveView {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

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

		if (this.walletService.getCurrentWallet().getMyAddresses().isEmpty()) {

			panel.html.setText("There are no addresses in this wallet yet");

		} else {

			Map<String, Object> events = new HashMap<>();
			events.put("addresses", walletService.getCurrentWallet().getMyAddresses());
			String html = velocity.process(events, "/html/receive.html");

			panel.html.setText(html);
		}

		panel.linkAddressBook.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainWindow.showAddressBook();
			}
		});

		/*
		 * panel.addressSelector.insertItemAt("No addresses yet", 0);
		 * panel.addressSelector.setSelectedIndex(0);
		 * 
		 * panel.btnCreateNewAddress.addActionListener(e -> {
		 * 
		 * SwingHelper.async(frame, "Getting new address", () -> { try {
		 * AddressDetailsResponse newAddress = zdp.getAddress();
		 * Alert.info("New address was created"); } catch (Exception e1) {
		 * Alert.error("Cannot connect to the network"); } });
		 * 
		 * });
		 */
		return panel;
		
	}

}
