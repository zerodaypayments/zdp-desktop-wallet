package io.zdp.wallet.desktop.ui.service;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;
import io.zdp.wallet.desktop.DesktopWallet;

@Service
public class DesktopWalletService extends WalletService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Wallet currentWallet;
	private File currentWalletFile;
	private String currentWalletPassword;

	@Value("${api.central.url}")
	private String apiCentralUrl;

	@Value("${api.url.wallet.new}")
	private String apiUrlWalletNew;

	@Value("${app.version}")
	private String appVersion;

	@Autowired
	private ConfigurationService configurationService;

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

	public void setCurrentWallet(Wallet w, File file, String pass) {

		this.currentWallet = w;
		this.currentWalletFile = file;
		this.currentWalletPassword = pass;

		if (file != null) {
			configurationService.getConfiguration().setLastWalletFile(file.getAbsolutePath());
			configurationService.saveConfiguration();
		}
	}

	public void saveCurrentWallet() {
		super.save(currentWalletFile, currentWallet, currentWalletPassword);
	}

	public Wallet getCurrentWallet() {
		return currentWallet;
	}

}
