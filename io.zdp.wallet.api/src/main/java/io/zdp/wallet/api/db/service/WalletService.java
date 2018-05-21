package io.zdp.wallet.api.db.service;

import io.zdp.wallet.api.db.domain.Account;
import io.zdp.wallet.api.db.domain.Wallet;

public interface WalletService {

	Wallet load();

	void create(Wallet w);
	
	Account addAccount(Wallet w, String privateKey, String curve);

	Account getAccount(String privateKey);

	void saveAccount(Account a);
	
}
