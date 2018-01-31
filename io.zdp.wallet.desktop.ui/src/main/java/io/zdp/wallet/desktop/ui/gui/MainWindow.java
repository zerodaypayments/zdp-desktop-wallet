package io.zdp.wallet.desktop.ui.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.desktop.DesktopWallet;
import io.zdp.wallet.desktop.ui.common.I18n;
import io.zdp.wallet.desktop.ui.common.Icons;
import io.zdp.wallet.desktop.ui.common.QTextComponentContextMenu;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.gui.action.CreateNewWallet;
import io.zdp.wallet.desktop.ui.gui.action.OpenWallet;
import io.zdp.wallet.desktop.ui.gui.dialog.AboutDialog;
import io.zdp.wallet.desktop.ui.gui.dialog.FreshStart;
import io.zdp.wallet.desktop.ui.gui.view.AddressBookView;
import io.zdp.wallet.desktop.ui.gui.view.HomeView;
import io.zdp.wallet.desktop.ui.gui.view.ReceiveView;
import io.zdp.wallet.desktop.ui.gui.view.SendView;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class MainWindow {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private JFrame frame;

	@Autowired
	private CreateNewWallet createNewWallet;

	@Autowired
	private OpenWallet openWallet;

	@Autowired
	private DesktopWallet desktopWallet;

	@Autowired
	private I18n i18n;

	@Autowired
	private DesktopWalletService walletService;

	private JPanel bgPanel;

	@Value("${app.url.online.faq}")
	private String appUrlOnlineFaq;

	@Value("${app.url.online.help}")
	private String appUrlOnlineHelp;

	private JPanel mainPanel;

	private JToolBar toolbar;

	private TrayIcon trayIcon;

	private JLabel statusLabel;

	@Autowired
	private AddressBookView addressBookView;

	@Autowired
	private SendView sendView;

	@Autowired
	private HomeView homeView;

	@Autowired
	private ReceiveView receiveView;

	@PostConstruct
	public void init() {
		// ((ZdpClientImpl)zdp).setHostUrl("http://localhost:8080");
	}

	public void run() {

		frame = new JFrame(i18n.get("app.window.title"));
		frame.setSize(900, 700);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setMinimumSize(new Dimension(320, 240));

		List<Image> icons = new ArrayList<>();

		icons.add(new ImageIcon(this.getClass().getResource("/icons/app/32.png")).getImage());
		icons.add(new ImageIcon(this.getClass().getResource("/icons/app/64.png")).getImage());
		icons.add(new ImageIcon(this.getClass().getResource("/icons/app/128.png")).getImage());
		icons.add(new ImageIcon(this.getClass().getResource("/icons/app/256.png")).getImage());
		icons.add(new ImageIcon(this.getClass().getResource("/icons/app/512.png")).getImage());

		frame.setIconImages(icons);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				desktopWallet.close();
			}
		});

		bgPanel = new JPanel() {

			BufferedImage bim;
			BufferedImage logo;

			{

				try {
					bim = ImageIO.read(this.getClass().getResourceAsStream("/images/bg.png"));
					logo = ImageIO.read(this.getClass().getResourceAsStream("/icons/zpg.png"));
				} catch (IOException e) {
					log.error("Error: ", e);
				}

				points = new ArrayList<>();

			}

			class NetworkPoint extends Point2D.Float {
				public NetworkPoint(float x, float y) {
					super(x, y);
				}

				public float accelX = 0;
				public float accelY = 0;
				public boolean deleted;
				public int alpha = 0;

			}

			private List<NetworkPoint> points = new ArrayList<>();

			@Override
			protected void paintComponent(Graphics g) {

				Graphics2D g2 = (Graphics2D) g;
				SwingHelper.awesomeGraphics(g2);

				if (points.isEmpty()) {

					for (int i = 0; i < 200; i++) {
						getNewPoint();
					}

				}

				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, getWidth(), getHeight());

				g2.setColor(Color.WHITE);
				Iterator<NetworkPoint> it = points.iterator();

				while (it.hasNext()) {

					NetworkPoint pt = it.next();

					//					g2.fillOval((int) pt.x - 2, (int) pt.y - 2, 4, 4);

					g2.setColor(new Color(255, 255, 255, pt.alpha));

					pt.x += pt.accelX;

					pt.y += pt.accelY;

					for (NetworkPoint otherPt : points) {
						if (pt != otherPt) {
							double dist = otherPt.distance(pt);
							if (dist <= 200) {
								int alpha = 100 - (int) dist;
								if (alpha < 0) {
									alpha = 0;
								}
								g2.setColor(new Color(255, 255, 255, alpha));
								g2.drawLine((int) pt.x, (int) pt.y, (int) otherPt.x, (int) otherPt.y);
							}
						}
					}

					g2.setColor(Color.WHITE);

					if (pt.x < -255 || pt.y < -255 || pt.x > getWidth() + 255 || pt.y > getHeight() + 255) {
						pt.deleted = true;
					}

				}

				it = points.iterator();
				int deleted = 0;
				while (it.hasNext()) {
					NetworkPoint pt = it.next();
					if (pt.deleted) {
						it.remove();
						deleted++;
					}
				}

				for (int i = 0; i < deleted; i++) {
					getNewPoint();
				}

				/*
				super.paintComponent(g);
				g.setColor(new Color(255, 168, 0));
				g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage(bim, 0, getHeight() - bim.getHeight(), null);
				g.drawImage(logo, (getWidth() - logo.getWidth()) / 2, (getHeight() - logo.getHeight()) / 2, null);
				*/

			}

			private void getNewPoint() {
				NetworkPoint p = new NetworkPoint(RandomUtils.nextInt(getWidth()), RandomUtils.nextInt(getHeight()));
				p.accelX = 1 * (RandomUtils.nextFloat() - RandomUtils.nextFloat());
				p.accelY = 1 * (RandomUtils.nextFloat() - RandomUtils.nextFloat());
				points.add(p);
			}

		};

		mainPanel = new JPanel(new BorderLayout());

		frame.add(mainPanel, BorderLayout.CENTER);

		addComponentToFrame(bgPanel);

		setupMainMenu();
		setupSystemTray();
		setupStatusBar();

		frame.setVisible(true);

		new Thread(() -> {

			while (true) {

				if (bgPanel.isVisible()) {

					SwingUtilities.invokeLater(() -> {
						bgPanel.repaint();
					});

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						log.error("Error: ", e);
					}
				}
			}
		}).start();

		// fresh start
		if (walletService.getRecentWallet() == null) {

			FreshStart freshStartPanel = new FreshStart();

			JDialog startDialog = SwingHelper.dialog(frame, freshStartPanel);
			startDialog.setTitle("Welcome to ZDP Wallet!");

			freshStartPanel.btnLoadExistingWallet.addActionListener(e -> {
				openWallet.open(startDialog, startDialog);
			});

			freshStartPanel.btnCreateNewWallet.addActionListener(e -> {

				createNewWallet.create(startDialog);

			});
			/*
			 * freshStartPanel.btnQuit.addActionListener(e -> {
			 * desktopWallet.close(); });
			 */

			SwingHelper.installEscapeCloseOperation(startDialog);
			startDialog.setVisible(true);

		}

	}

	private void setupStatusBar() {

		JPanel statusPanel = new JPanel(new BorderLayout());

		statusLabel = new JLabel("Starting...");
		statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		statusPanel.add(statusLabel);

		this.frame.add(statusPanel, BorderLayout.PAGE_END);

	}

	public void setStatusMessage(String status, Icon icon) {
		SwingUtilities.invokeLater(() -> {
			this.statusLabel.setText(status);
			this.statusLabel.setIcon(icon);
		});
	}

	public void showSystemTrayMessage(MessageType type, String msg) {
		trayIcon.displayMessage("ZDP Wallet Message", msg, type);
	}

	private void setupSystemTray() {

		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		trayIcon = new TrayIcon(Icons.load("app/64.png"), "ZDP Wallet");
		trayIcon.setImageAutoSize(true);

		final SystemTray tray = SystemTray.getSystemTray();

		final PopupMenu popup = new PopupMenu();
		trayIcon.setPopupMenu(popup);

		MenuItem newWalletItem = new MenuItem("Send");
		newWalletItem.addActionListener(e -> {
			showSendScreen();
			SwingHelper.bringToFront(frame);
		});
		popup.add(newWalletItem);

		MenuItem openWalletItem = new MenuItem("Receive");
		openWalletItem.addActionListener(e -> {
			showReceiveScreen();
			SwingHelper.bringToFront(frame);
		});
		popup.add(openWalletItem);

		popup.addSeparator();

		MenuItem exitItem = new MenuItem("Exit");
		exitItem.addActionListener(e -> {
			desktopWallet.close();
		});
		popup.add(exitItem);

		try {
			tray.add(trayIcon);
		} catch (Exception e) {
			System.out.println("TrayIcon could not be added.");
		}

	}

	private void setupMainMenu() {

		JMenuBar mb = new JMenuBar();

		{
			JMenu menu = new JMenu("Wallet");
			mb.add(menu);

			JMenuItem newWalletMenu = new JMenuItem("New wallet", 'N');
			newWalletMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
			menu.add(newWalletMenu);
			newWalletMenu.addActionListener(e -> {
				createNewWallet.create(frame);
			});

			// menu.add(new JMenu("Recent wallets"));

			menu.addSeparator();

			JMenuItem menuOpenWallet = new JMenuItem("Open wallet", Icons.getIcon("24/open.png"));
			menuOpenWallet.setMnemonic('O');
			menuOpenWallet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
			menuOpenWallet.addActionListener(e -> {
				openWallet.open(frame, null);
			});

			menu.add(menuOpenWallet);
			menu.addSeparator();

			JMenuItem itemCloseWallet = new JMenuItem("Close wallet", 'c');
			itemCloseWallet.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));

			itemCloseWallet.addActionListener(e -> {
				closeWallet();
			});
			menu.add(itemCloseWallet);
			menu.addSeparator();

			JMenuItem exit = new JMenuItem("Exit", 'x');
			exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));

			exit.addActionListener(e -> {
				desktopWallet.close();
			});
			menu.add(exit);
		}

		{
			JMenu menu = new JMenu("Help");
			mb.add(menu);

			JMenuItem menuOnlineHelp = new JMenuItem("Online help", Icons.getIcon("menu/information.png"));
			menu.add(menuOnlineHelp);
			menuOnlineHelp.addActionListener(e -> {
				SwingHelper.browseToUrl(appUrlOnlineHelp);
			});

			JMenuItem menuFaq = new JMenuItem("FAQ");
			menu.add(menuFaq);
			menuFaq.addActionListener(e -> {
				SwingHelper.browseToUrl(appUrlOnlineFaq);
			});
			menu.add(menuFaq);

			menu.addSeparator();

			JMenuItem aboutMenu = new JMenuItem("About", Icons.getIcon("app/16.png"));
			aboutMenu.addActionListener(e -> {
				AboutDialog aboutDialogPanel = new AboutDialog();
				JDialog aboutDialog = SwingHelper.dialog(frame, aboutDialogPanel);
				aboutDialogPanel.btnOk.addActionListener(ex -> {
					aboutDialog.dispose();
				});
				new QTextComponentContextMenu(aboutDialogPanel.text);
				aboutDialog.setLocationRelativeTo(null);
				SwingHelper.installEscapeCloseOperation(aboutDialog);
				aboutDialog.setTitle("About " + i18n.get("app.window.title"));
				aboutDialog.setVisible(true);
			});
			menu.add(aboutMenu);
		}

		frame.setJMenuBar(mb);

	}

	public void setWallet(Wallet w, File walletFile, String pass) {

		walletService.setCurrentWallet(w, walletFile, pass);

		updateFrame(w);

		initUI();

	}

	public void updateFrame(Wallet w) {

		frame.setTitle(i18n.get("app.window.title"));

	}

	private void setupToolbar() {

		if (toolbar == null) {

			toolbar = new JToolBar();

			JButton btnHome = new JButton("Home", new ImageIcon(this.getClass().getResource("/icons/home.png")));
			btnHome.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnHome.setHorizontalTextPosition(SwingConstants.CENTER);
			toolbar.add(btnHome);
			btnHome.addActionListener(e -> {
				showHomeScreen();
			});

			JButton btnSend = new JButton("Send", new ImageIcon(this.getClass().getResource("/icons/send.png")));
			btnSend.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnSend.setHorizontalTextPosition(SwingConstants.CENTER);
			toolbar.add(btnSend);

			btnSend.addActionListener(e -> {
				showSendScreen();
			});

			JButton btnReceive = new JButton("Receive", new ImageIcon(this.getClass().getResource("/icons/receive.png")));
			btnReceive.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnReceive.setHorizontalTextPosition(SwingConstants.CENTER);
			toolbar.add(btnReceive);

			btnReceive.addActionListener(e -> {
				showReceiveScreen();
			});

			JButton btnTransactions = new JButton("Transactions", new ImageIcon(this.getClass().getResource("/icons/transactions.png")));
			btnTransactions.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnTransactions.setHorizontalTextPosition(SwingConstants.CENTER);
			btnTransactions.addActionListener(e -> {

			});
			toolbar.add(btnTransactions);

			JButton btnAddressBook = new JButton("Address Book", new ImageIcon(this.getClass().getResource("/icons/address_book.png")));
			btnAddressBook.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnAddressBook.setHorizontalTextPosition(SwingConstants.CENTER);
			btnAddressBook.addActionListener(e -> {
				showAddressBook();
			});
			toolbar.add(btnAddressBook);

			toolbar.add(Box.createHorizontalGlue());

			JButton btnSync = new JButton("Synchronize", new ImageIcon(this.getClass().getResource("/icons/refresh.png")));
			btnSync.setVerticalTextPosition(SwingConstants.BOTTOM);
			btnSync.setHorizontalTextPosition(SwingConstants.CENTER);
			btnSync.addActionListener(e -> {
				try {
					showSystemTrayMessage(MessageType.INFO, "TODO Wallet synchronized");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			toolbar.add(btnSync);

			frame.add(toolbar, BorderLayout.PAGE_START);

		}
	}

	/**
	 * Address book consists of 2 tabs -> my addresses I can send from and
	 * recepient's addresses I can send to
	 */
	public void showAddressBook() {
		this.addComponentToFrame(addressBookView.get());
	}

	/**
	 * Open 'Send' screen
	 */
	private void showSendScreen() {
		this.addComponentToFrame(sendView.get());
	}

	/**
	 * Open 'Receive' screen
	 */
	private void showReceiveScreen() {
		addComponentToFrame(receiveView.get());
	}

	private void initUI() {
		showHomeScreen();
		setupToolbar();
	}

	public void showHomeScreen() {
		addComponentToFrame(this.homeView.get());
		setupToolbar();
	}

	private void addComponentToFrame(JComponent c) {

		mainPanel.removeAll();
		mainPanel.doLayout();
		mainPanel.revalidate();
		mainPanel.repaint();

		mainPanel.add(c, BorderLayout.CENTER);

	}

	private void closeWallet() {

		if (walletService.getCurrentWallet() != null) {

			walletService.setCurrentWallet(null, null, null);

			frame.getContentPane().removeAll();
			frame.add(bgPanel, BorderLayout.CENTER);
			frame.revalidate();
			frame.doLayout();
			frame.repaint();
		}
	}

	public JFrame getFrame() {
		return frame;
	}

}
