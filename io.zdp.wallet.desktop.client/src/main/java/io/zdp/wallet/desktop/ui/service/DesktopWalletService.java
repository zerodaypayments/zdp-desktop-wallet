package io.zdp.wallet.desktop.ui.service;

import java.io.File;
import java.math.BigDecimal;
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

import io.zdp.api.model.v1.GetBalanceResponse;
import io.zdp.api.model.v1.GetTransactionDetailsResponse;
import io.zdp.crypto.Curves;
import io.zdp.crypto.key.ZDPKeyPair;
import io.zdp.wallet.api.db.domain.Account;
import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.api.service.WalletApiService;
import io.zdp.wallet.desktop.DesktopWallet;
import io.zdp.wallet.desktop.ui.gui.MainWindow;

@Service
public class DesktopWalletService {

	private final Logger log = LoggerFactory.getLogger( this.getClass() );

	@Value ( "${api.central.url}" )
	private String apiCentralUrl;

	@Value ( "${api.url.wallet.new}" )
	private String apiUrlWalletNew;

	@Value ( "${app.version}" )
	private String appVersion;

	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	private MainWindow mainWindow;

	private Map < Wallet, List < GetTransactionDetailsResponse > > walletTransactions = new HashMap<>();

	@Autowired
	private WalletApiService walletService;

	@PostConstruct
	public void init ( ) {
	}

	public DesktopWallet getRecentWallet ( ) {

		if ( StringUtils.isNotBlank( configurationService.getConfiguration().getLastWalletFile() ) ) {

			// TOD ask for password and load

		} else {
		}
		return null;
	}

	public void setCurrentWallet ( Wallet w, File file ) {

		if ( file != null ) {
			configurationService.getConfiguration().setLastWalletFile( file.getAbsolutePath() );
			configurationService.saveConfiguration();
		}
	}

	public Wallet getCurrentWallet ( ) {
		return walletService.getCurrentWallet();
	}

	public Wallet create ( String password, File file, ZDPKeyPair kp ) throws Exception {

		Wallet wallet = this.walletService.openWallet( file, password );

		this.walletService.getWalletService().addAccount( wallet, kp.getPrivateKeyAsBase58(), Curves.DEFAULT_CURVE );

		return wallet;

	}

	public void saveAccountDetails ( Account account, GetBalanceResponse resp ) {
		walletService.getWalletService().updateAccountDetails( account, new BigDecimal( resp.getAmount() ), resp.getHeight(), resp.getChainHash() );
	}

}
