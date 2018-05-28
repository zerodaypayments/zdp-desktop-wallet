package io.zdp.wallet.desktop.ui.job;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.zdp.client.ZdpClient;
import io.zdp.model.network.NetworkNode;
import io.zdp.model.network.NetworkTopologyService;
import io.zdp.wallet.desktop.ui.common.Icons;
import io.zdp.wallet.desktop.ui.gui.MainWindow;
import io.zdp.wallet.desktop.ui.service.DesktopWalletService;

@Component
public class NetworkStatusCheckingJob {

	private final Logger log = LoggerFactory.getLogger( this.getClass() );

	@Autowired
	private MainWindow mainWindow;

	@Autowired
	private DesktopWalletService walletService;

	@Autowired
	private ZdpClient zdp;

	@Autowired
	private NetworkTopologyService networkService;

	private boolean connected;

	private NetworkNode node;

	@PostConstruct
	public void init ( ) {

		node = networkService.getRandomNode();
		zdp.setNetworkNode( node );

	}

	@Scheduled ( fixedDelay = DateUtils.MILLIS_PER_SECOND * 5 )
	public void check ( ) throws Exception {

		if ( connected == false ) {
			ImageIcon icon = new ImageIcon( this.getClass().getResource( "/icons/ajax-loader.gif" ) );
			mainWindow.setStatusMessage( "Checking network connection (" + zdp.getHostUrl() + ")", icon );
		}

		try {

			zdp.ping();

			connected = true;

			mainWindow.setStatusMessage( "Connected to network (" + zdp.getHostUrl() + ")", Icons.getIcon( "check.png" ) );

		} catch ( Exception e ) {

			log.error( "Error: " + e.getMessage() );

			mainWindow.setStatusMessage( "Network not available (" + zdp.getHostUrl() + ")", Icons.getIcon( "cancel.png" ) );

			connected = false;

			NetworkNode newNode = node;
			while ( newNode != null && newNode.equals( node ) ) {
				node = networkService.getRandomNode();
				zdp.setNetworkNode( node );
				check();
			}
		}

	}

	public boolean isConnected ( ) {
		return connected;
	}

}
