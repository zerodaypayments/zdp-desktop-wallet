package io.zdp.wallet.desktop.api.service;

import java.io.File;

import io.zdp.wallet.desktop.api.domain.Wallet;

public interface WalletService {

	Wallet create(File file, String name, char[] password);

	Wallet load(File file, char[] pass);

	void save(Wallet wallet, char[] pass);

}
