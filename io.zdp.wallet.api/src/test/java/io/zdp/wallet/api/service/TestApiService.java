package io.zdp.wallet.api.service;

import java.io.File;
import java.math.BigDecimal;

import org.bouncycastle.util.Arrays;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.zdp.crypto.Curves;
import io.zdp.crypto.key.ZDPKeyPair;
import io.zdp.wallet.api.db.domain.Account;
import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.api.db.service.WalletService;
import junit.framework.TestCase;

@RunWith ( SpringJUnit4ClassRunner.class )
@ContextConfiguration ( "classpath:/wallet-api-spring-context.xml" )
public class TestApiService extends TestCase {

	@Autowired
	private WalletApiService apiService;

	@Test
	public void test ( ) throws Exception {

		assertNotNull( apiService.getApiVersion() );

		{

			Wallet wallet = apiService.openWallet( new File( "wallet" ), "666666" );

			System.out.println( wallet );

			apiService.closeWallet();

		}

		{

			Wallet wallet = apiService.openWallet( new File( "wallet" ), "666666" );

			ZDPKeyPair kp = ZDPKeyPair.createRandom( Curves.DEFAULT_CURVE );

			Account a = apiService.getWalletService().addAccount( wallet, kp.getPrivateKeyAsBase58(), Curves.DEFAULT_CURVE );

			assertEquals( BigDecimal.ZERO, a.getBalance() );
			assertEquals( Curves.DEFAULT_CURVE_INDEX, a.getCurve() );
			assertEquals( 0, a.getHeight() );
			assertEquals( kp.getPrivateKeyAsBase58(), a.getPrivateKey() );
			assertEquals( kp.getPublicKeyAsBase58(), a.getPublicKey() );
			assertTrue( Arrays.areEqual( new byte [ ] {}, a.getTransferChainHash() ) );
			assertTrue( Arrays.areEqual( kp.getZDPAccount().getPublicKeyHash(), a.getUuid() ) );
			assertEquals( kp.getZDPAccount().getUuid(), a.getZdpUuid() );

		}

	}

}
