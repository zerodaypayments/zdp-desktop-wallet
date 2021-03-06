package io.zdp.wallet.desktop;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang3.SystemUtils;
import org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import io.zdp.wallet.desktop.ui.common.Fonts;
import io.zdp.wallet.desktop.ui.gui.MainWindow;

@Component
public class DesktopWallet {

	private static final Logger log = LoggerFactory.getLogger(DesktopWallet.class);

	private static ClassPathXmlApplicationContext ctx;

	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			// UIManager.setLookAndFeel(new SubstanceOfficeSilver2007LookAndFeel());
/*
			if (SystemUtils.IS_OS_WINDOWS) {

				UIManager.setLookAndFeel(new SubstanceMarinerLookAndFeel());

				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);

			} else if (SystemUtils.IS_OS_LINUX) {

				System.setProperty("sun.java2d.opengl", "true");

				UIManager.setLookAndFeel(new SubstanceMarinerLookAndFeel());

			} else if (SystemUtils.IS_OS_MAC_OSX) {

				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "ZDP Wallet");

				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			} else {

				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			}
*/
		} catch (Exception e1) {
			log.error("Error: ", e1);
		}

		SwingUtilities.invokeLater(() -> {

			// Override fonts
			if (SystemUtils.IS_OS_WINDOWS) {
				try {
					//setUIFont(new javax.swing.plaf.FontUIResource(Fonts.getMainFont()));
				} catch (Exception e) {
					log.error("Error: ", e);
				}
			}

			ctx = new ClassPathXmlApplicationContext("classpath:/io/zdp/wallet/desktop/spring-context.xml");
			ctx.setDisplayName("DesktopWallet Context");

			ctx.start();

			ctx.getBean(MainWindow.class).run();

		});
	}

	public void close() {
		try {
			ctx.stop();
			ctx.close();
		} catch (Exception e) {
			log.error("Error: ", e);
		}
		System.exit(0);
	}

	private static void setUIFont(javax.swing.plaf.FontUIResource f) {
		java.util.Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value != null && value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, f);
		}
	}

}
