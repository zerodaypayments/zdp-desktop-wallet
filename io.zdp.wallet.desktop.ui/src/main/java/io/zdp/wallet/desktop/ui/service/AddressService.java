package io.zdp.wallet.desktop.ui.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.zdp.client.ZdpClient;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.domain.WalletAddress;
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

	public WalletAddress createNewAddress() {

		WalletAddress address = null;
/*
		try {

			URI uri = new URI(apiCentralUrl + apiUrlAddressNew);
			log.debug("Create new address: " + uri);
			AddressDetailsResponse resp = restTemplate.postForObject(uri, null, AddressDetailsResponse.class);

			address = new WalletAddress();
			address.setAddress(resp.getAddress());
			address.setPrivateKey(resp.getPrivateKey());
			address.setSeed(resp.getSecret());

		} catch (URISyntaxException e) {
			log.error("Error: ", e);
		}
*/
		return address;
	}
	
	public void generateNewAddress() {
		walletService.generateNewAddress();
		mainWindow.showAddressBook();
	}
	

}
