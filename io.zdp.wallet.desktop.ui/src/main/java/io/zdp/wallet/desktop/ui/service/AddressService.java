package io.zdp.wallet.desktop.ui.service;

import java.awt.TrayIcon.MessageType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.zdp.api.model.AddressDetailsResponse;
import io.zdp.client.ZdpClient;
import io.zdp.wallet.desktop.api.domain.MyAddress;
import io.zdp.wallet.desktop.api.domain.Wallet;
import io.zdp.wallet.desktop.api.domain.WalletEvent;
import io.zdp.wallet.desktop.ui.common.Alert;
import io.zdp.wallet.desktop.ui.common.SwingHelper;
import io.zdp.wallet.desktop.ui.gui.MainWindow;

@Service
public class AddressService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private RestTemplate restTemplate;

	@Value("${api.central.url}")
	private String apiCentralUrl;

	@Value("${api.url.address.new}")
	private String apiUrlAddressNew;

	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private MainWindow mainWindow;
	
	@Autowired
	private DesktopWalletService walletService;
	
	@Autowired
	private ZdpClient zdp;

	@PostConstruct
	public void init() {
		restTemplate = new RestTemplate();
	}

	public Wallet getRecentWallet() {
		if (StringUtils.isNotBlank(configurationService.getConfiguration().getLastWalletFile())) {

			// TOD ask for password and load

		} else {
		}
		return null;
	}

	public MyAddress createNewAddress() {

		MyAddress address = null;

		try {

			URI uri = new URI(apiCentralUrl + apiUrlAddressNew);
			log.debug("Create new address: " + uri);
			AddressDetailsResponse resp = restTemplate.postForObject(uri, null, AddressDetailsResponse.class);

			address = new MyAddress();
			address.setAddress(resp.getAddress());
			address.setPrivateKey(resp.getPrivateKey());
			address.setSeed(resp.getSecret());

		} catch (URISyntaxException e) {
			log.error("Error: ", e);
		}

		return address;
	}
	
	public void generateNewAddress(Runnable successCallback) {

		SwingHelper.async(mainWindow.getFrame(), "Getting new address", () -> {
			
			try {

				AddressDetailsResponse newAddress = zdp.getAddress();

				// Alert.info("New address was generated");
				mainWindow.showSystemTrayMessage(MessageType.INFO, "New address was generated: " + newAddress.getAddress());

				MyAddress a = new MyAddress();
				a.setAddress(newAddress.getAddress());
				a.setDescription("newly generated address");
				a.setPrivateKey(newAddress.getPrivateKey());
				a.setSeed(newAddress.getSecret());
				a.setBalance(newAddress.getBalance());

				this.walletService.getCurrentWallet().getMyAddresses().add(a);

				WalletEvent e = new WalletEvent();
				e.setDate(new Date());
				e.setType(WalletEvent.ADDRESS_GENERATED);
				e.setMessage(a.getAddress());
				this.walletService.getCurrentWallet().getWalletEvents().add(e);

				this.walletService.saveCurrentWallet();

				successCallback.run();

			} catch (Exception e1) {
				e1.printStackTrace();
				Alert.error("Cannot connect to the network");
			}
		});

	}
	

}
