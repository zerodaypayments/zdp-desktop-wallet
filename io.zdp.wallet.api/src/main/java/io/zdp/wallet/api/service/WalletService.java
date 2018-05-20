package io.zdp.wallet.api.service;

import java.io.File;
import java.util.List;

import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.domain.AccountTransaction;

public interface WalletService {

	Wallet create(String privKey, File file) throws Exception;

	void save(File file, Wallet wallet) throws Exception;

	Wallet load(File file) throws Exception;

	void saveTransaction(File file, AccountTransaction tx) throws Exception;

	AccountTransaction getTransactionByUuid(File file, String uuid) throws Exception;

	long countTransactions(File file) throws Exception;

	List<AccountTransaction> listTransactions(int page, int size) throws Exception;

}
