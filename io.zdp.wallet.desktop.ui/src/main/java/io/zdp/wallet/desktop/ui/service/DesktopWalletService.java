package io.zdp.wallet.desktop.ui.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.zdp.api.model.v1.GetTransactionDetailsResponse;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;
import io.zdp.wallet.desktop.DesktopWallet;
import io.zdp.wallet.desktop.ui.gui.MainWindow;

@Service
public class DesktopWalletService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Wallet currentWallet;
	private File currentWalletFile;

	@Value("${api.central.url}")
	private String apiCentralUrl;

	@Value("${api.url.wallet.new}")
	private String apiUrlWalletNew;

	@Value("${app.version}")
	private String appVersion;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private MainWindow mainWindow;

	private Map<Wallet, List<GetTransactionDetailsResponse>> walletTransactions = new HashMap<>();

	@Autowired
	private WalletService walletService;
	
	@PostConstruct
	public void init() {
	}

	public DesktopWallet getRecentWallet() {

		if (StringUtils.isNotBlank(configurationService.getConfiguration().getLastWalletFile())) {

			// TOD ask for password and load

		} else {
		}
		return null;
	}

	public void setCurrentWallet(Wallet w, File file) {

		this.currentWallet = w;
		this.currentWalletFile = file;

		if (file != null) {
			configurationService.getConfiguration().setLastWalletFile(file.getAbsolutePath());
			configurationService.saveConfiguration();
		}
	}

	public void saveCurrentWallet() throws Exception {
		walletService.save(currentWalletFile, currentWallet);
	}

	public Wallet getCurrentWallet() {
		return currentWallet;
	}

	public Wallet create(String privKey, File file) throws Exception {
		return this.walletService.create(privKey, file);
	}

}
