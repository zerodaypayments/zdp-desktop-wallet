package io.zdp.wallet.desktop.ui.job;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.Icon;
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

	private ImageIcon icon = new ImageIcon( this.getClass().getResource( "/icons/ajax-loader.gif" ) );

	private Icon checkIcon = Icons.getIcon( "check.png" );

	private Icon cancelIcon = Icons.getIcon( "cancel.png" );

	@PostConstruct
	public void init ( ) {

		node = networkService.getRandomNode();
		zdp.setNetworkNode( node );

	}

	@Scheduled ( fixedDelay = DateUtils.MILLIS_PER_SECOND * 5 )
	public void check ( ) throws Exception {

		if ( connected == false ) {
			mainWindow.setStatusMessage( "Checking network connection (" + zdp.getHostUrl() + ")", icon );
		}

		try {

			zdp.ping();

			connected = true;

			mainWindow.setStatusMessage( "Connected to network (" + zdp.getHostUrl() + ")", checkIcon );

		} catch ( Exception e ) {

			log.error( "Error: " + e.getMessage() );

			mainWindow.setStatusMessage( "Network not available (" + zdp.getHostUrl() + ")", cancelIcon );

			connected = false;

			// Go throught all the nodes, find the one that can be pinged
			List < NetworkNode > allNodes = this.networkService.getAllNodes();

			Collections.shuffle( allNodes );

			for ( NetworkNode node : allNodes ) {
				
				zdp.setNetworkNode( node );
				
				try {
					
					log.debug( "Connect to: " + node );
					
					mainWindow.setStatusMessage( "Checking network connection (" + zdp.getHostUrl() + ")", icon );
					
					zdp.ping();
					
					log.debug( "Connected to: " + node );
					
					return;
					
				} catch ( Exception e1 ) {
					log.error( "Can't connect to: " + node );
					Thread.sleep( 1000 );
				}

			}
			
			mainWindow.setStatusMessage( "Network not available, will re-try shortly...", cancelIcon );
		}

	}

	public boolean isConnected ( ) {
		return connected;
	}

}
