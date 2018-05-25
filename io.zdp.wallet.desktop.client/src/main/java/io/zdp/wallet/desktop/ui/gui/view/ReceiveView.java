package io.zdp.wallet.desktop.ui.gui.view;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.ByteArrayOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.TextComponentFocuser;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.gui.dialog.ReceivePanel;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@Component
public class ReceiveView {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private MainWindow mainWindow;

	public JPanel get() {

		ReceivePanel panel = new ReceivePanel();

		try {

			final String publicKey58 = walletService.getCurrentWallet().getAccounts().get( 0 ).getZdpUuid();

			panel.txtAddress.setText(publicKey58);

			new QTextComponentContextMenu(panel.txtAddress);
			panel.txtAddress.addFocusListener(new TextComponentFocuser());
			panel.txtAddress.setCaretPosition(0);

			panel.panelCenter.setLayout(new BorderLayout());

			panel.panelCenter.add(new JPanel() {

				@Override
				protected void paintComponent(Graphics g) {

					ByteArrayOutputStream os = QRCode.from(publicKey58).withSize(getWidth(), getHeight()).to(ImageType.PNG).stream();

					ImageIcon imageData = new ImageIcon(os.toByteArray());

					g.drawImage(imageData.getImage(), (getWidth() - imageData.getIconWidth()) / 2, (getHeight() - imageData.getIconHeight()) / 2, null);

				}

			}, BorderLayout.CENTER);

		} catch (Exception e) {
			log.error("Error: ", e);
		}

		// start background transfer listener

		return panel;

	}

}
