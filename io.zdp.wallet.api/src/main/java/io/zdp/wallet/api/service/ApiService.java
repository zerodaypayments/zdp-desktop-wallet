package io.zdp.wallet.api.service;

import java.io.File;

import io.zdp.wallet.api.db.domain.Account;
import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.api.db.service.WalletService;

public interface ApiService {

	Wallet openWallet(File file, String password) throws Exception;
	
	String getApiVersion();
	
	void closeWallet();
	
	boolean isAccountExisting(String privateKey);

	WalletService getWalletService();

	Wallet getCurrentWallet();
	
	
/*
	void save(File file, Wallet wallet) throws Exception;

	Wallet load(File file) throws Exception;

	void saveTransaction(File file, AccountTransaction tx) throws Exception;

	AccountTransaction getTransactionByUuid(File file, String uuid) throws Exception;

	long countTransactions(File file) throws Exception;

	List<AccountTransaction> listTransactions(int page, int size) throws Exception;
*/
}
