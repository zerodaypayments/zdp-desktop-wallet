package io.zdp.wallet.api.service;

import java.io.File;

import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.api.db.service.WalletService;

public interface WalletApiService {
	
	boolean isWalletFile(File file, String password);

	Wallet openWallet ( File file, String password ) throws Exception;

	String getApiVersion ( );

	void closeWallet ( );

	boolean isAccountExisting ( String privateKey );

	WalletService getWalletService ( );

	Wallet getCurrentWallet ( );

}
