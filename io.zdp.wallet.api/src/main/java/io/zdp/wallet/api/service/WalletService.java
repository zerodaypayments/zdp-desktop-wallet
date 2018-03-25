package io.zdp.wallet.api.service;

import java.io.File;

import io.zdp.wallet.api.domain.Wallet;

public interface WalletService {

	Wallet create(String privKey, File file) throws Exception;

	void save(File file, Wallet wallet) throws Exception;

	Wallet load(File file) throws Exception;

}
