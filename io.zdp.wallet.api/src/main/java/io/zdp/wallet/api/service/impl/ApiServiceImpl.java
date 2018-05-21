package io.zdp.wallet.api.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.nio.channels.IllegalChannelGroupException;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.h2.store.fs.FilePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import io.zdp.crypto.Curves;
import io.zdp.crypto.key.ZDPKeyPair;
import io.zdp.wallet.api.db.common.FilePathTestWrapper;
import io.zdp.wallet.api.db.common.H2Helper;
import io.zdp.wallet.api.db.domain.Account;
import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.api.db.service.WalletService;
import io.zdp.wallet.api.service.ApiService;

@Service
public class ApiServiceImpl implements ApiService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private ClassPathXmlApplicationContext ctx;

	private Wallet currentWallet;

	private WalletService walletService;

	@Value("${wallet.api.version}")
	private String walletApiVersion;

	@Value("${jdbc.url}")
	private String jdbcUrl;

	@Override
	public Wallet openWallet(File file, String password) throws Exception {

		// close current wallet if any
		closeWallet();

		FilePathTestWrapper wrapper = new FilePathTestWrapper();
		FilePath.register(wrapper);

		// Validate DB
		if (false == H2Helper.isValidH2Database(file, password)) {
			throw new IllegalArgumentException("Not an H2 database");
		}

		System.setProperty("zdp.wallet.file", file.getAbsolutePath());
		System.setProperty("zdp.wallet.password", password);

		ctx = new ClassPathXmlApplicationContext("classpath:/spring-wallet-context.xml");
		ctx.setDisplayName("WalletContext");
		ctx.start();

		log.debug("Load current wallet: " + jdbcUrl);

		walletService = ctx.getBean(WalletService.class);

		this.currentWallet = walletService.load();

		if (this.currentWallet == null) {

			log.debug("Create wallet");

			this.currentWallet = new Wallet();
			this.currentWallet.setDate(new Date());
			this.currentWallet.setUuid(UUID.randomUUID().toString());
			this.currentWallet.setVersion(walletApiVersion);

			walletService.create(this.currentWallet);

		}

		log.debug("Wallet: " + this.currentWallet);

		return this.currentWallet;
	}

	public Wallet getCurrentWallet() {
		return currentWallet;
	}

	@PreDestroy
	public void closeWallet() {

		if (ctx != null && ctx.isRunning()) {
			ctx.stop();
			ctx.close();
		}

	}

	@Override
	public String getApiVersion() {
		return walletApiVersion;
	}

	@Override
	public boolean isAccountExisting(String privateKey) {
		return walletService.getAccount(privateKey) != null;
	}

	@Override
	public WalletService getWalletService() {
		return walletService;
	}

}
