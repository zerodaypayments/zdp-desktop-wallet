package io.zdp.wallet.desktop.ui.service;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.zdp.wallet.desktop.api.domain.Wallet;
import io.zdp.wallet.desktop.api.service.impl.WalletServiceImpl;

@Service
public class DesktopWalletService extends WalletServiceImpl {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Wallet currentWallet;
	private File currentWalletFile;
	private char[] currentWalletPassword;

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

	public Wallet getRecentWallet() {

		if (StringUtils.isNotBlank(configurationService.getConfiguration().getLastWalletFile())) {

			// TOD ask for password and load

		} else {
		}
		return null;
	}

	public void setCurrentWallet(Wallet w, File file, char[] pass) {

		this.currentWallet = w;
		this.currentWalletFile = file;
		this.currentWalletPassword = pass;

		if (file != null) {
			configurationService.getConfiguration().setLastWalletFile(file.getAbsolutePath());
			configurationService.saveConfiguration();
		}
	}

	public void saveCurrentWallet() {
		this.save(currentWallet, currentWalletPassword);
	}

	public Wallet getCurrentWallet() {
		return currentWallet;
	}

}
