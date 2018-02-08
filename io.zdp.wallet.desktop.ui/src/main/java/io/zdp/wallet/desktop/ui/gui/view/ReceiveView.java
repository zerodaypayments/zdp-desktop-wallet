package io.zdp.wallet.desktop.ui.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.common.crypto.Signer;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.common.TextComponentFocuser;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.ReceivePanel;
import io.zdp.wallet.desktop.ui.service.AddressService;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@Component
public class ReceiveView {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AddressService addressService;

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	public JPanel get() {

		ReceivePanel panel = new ReceivePanel();

		try {
			final String walletUuid = Signer.getPublicKeyHash(walletService.getCurrentWallet().getPublicKey());
			final String address = CryptoUtils.getUniqueAddressForAccountUuid(walletUuid);

			panel.txtAddress.setText(address);

			new QTextComponentContextMenu(panel.txtAddress);
			panel.txtAddress.addFocusListener(new TextComponentFocuser());
			panel.txtAddress.setCaretPosition(0);

			panel.panelCenter.setLayout(new BorderLayout());

			panel.panelCenter.add(new JPanel() {

				@Override
				protected void paintComponent(Graphics g) {

					ByteArrayOutputStream os = QRCode.from(address).withSize(getWidth(), getHeight()).to(ImageType.PNG).stream();

					ImageIcon imageData = new ImageIcon(os.toByteArray());

					g.drawImage(imageData.getImage(), (getWidth() - imageData.getIconWidth()) / 2, (getHeight() - imageData.getIconHeight()) / 2, null);

				}

			}, BorderLayout.CENTER);

		} catch (Exception e) {
			log.error("Error: ", e);
		}

		/*
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
		*/

		return panel;

	}

}
