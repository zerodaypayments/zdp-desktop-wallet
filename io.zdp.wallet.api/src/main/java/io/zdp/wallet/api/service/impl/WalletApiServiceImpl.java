package io.zdp.wallet.api.service.impl;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import io.zdp.wallet.api.db.common.H2Helper;
import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.api.db.service.WalletService;
import io.zdp.wallet.api.service.WalletApiService;

@Service
public class WalletApiServiceImpl implements WalletApiService {

	private final Logger log = LoggerFactory.getLogger( this.getClass() );

	private ClassPathXmlApplicationContext ctx;

	private Wallet currentWallet;

	private WalletService walletService;

	@Value ( "${wallet.api.version}" )
	private String walletApiVersion;

	@Override
	public boolean isWalletFile ( File file, String password ) {
		return H2Helper.isValidH2Database( file, password );
	}

	@Override
	public Wallet openWallet ( File file, String password ) throws Exception {

		password = password + " " + password;

		// close current wallet if any
		closeWallet();

		// Validate DB
		if ( false == this.isWalletFile( file, password ) ) {
			throw new IllegalArgumentException( "Not an H2 database" );
		}

		System.setProperty( "zdp.wallet.file", StringUtils.removeEnd( file.getAbsolutePath(), ".mv.db" ) );
		System.setProperty( "zdp.wallet.password", password );

		ctx = new ClassPathXmlApplicationContext( "classpath:/spring-wallet-context.xml" );
		ctx.setDisplayName( "WalletContext" );
		ctx.start();

		walletService = ctx.getBean( WalletService.class );

		this.currentWallet = walletService.load();

		if ( this.currentWallet == null ) {

			log.debug( "Create wallet" );

			this.currentWallet = new Wallet();
			this.currentWallet.setDate( new Date() );
			this.currentWallet.setUuid( UUID.randomUUID().toString() );
			this.currentWallet.setVersion( walletApiVersion );

			walletService.create( this.currentWallet );

		}

		log.debug( "Wallet: " + this.currentWallet );

		return this.currentWallet;
	}

	public Wallet getCurrentWallet ( ) {
		return currentWallet;
	}

	@PreDestroy
	public void closeWallet ( ) {

		log.debug( "Close wallet" );

		if ( ctx != null && ctx.isRunning() ) {
			ctx.stop();
			ctx.close();
		}

	}

	@Override
	public String getApiVersion ( ) {
		return walletApiVersion;
	}

	@Override
	public boolean isAccountExisting ( String privateKey ) {
		return walletService.getAccount( privateKey ) != null;
	}

	@Override
	public WalletService getWalletService ( ) {
		return walletService;
	}

}
