package io.zdp.wallet.desktop.api.service;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;
import junit.framework.TestCase;

public class TestWalletService extends TestCase {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void test() throws Exception {

		File file = new File("1.wallet");

		String privKey = CryptoUtils.generateRandomNumber256bits();
		
		System.out.println(privKey);

		Wallet w1 = WalletService.create(privKey, file);

		assertNotNull(w1);
		assertTrue(w1.getAddresses().isEmpty());
		assertFalse(w1.getSeed().isEmpty());
		assertFalse(w1.getUuid().isEmpty());

		System.out.println(w1);

		{
			Wallet w2 = WalletService.load(file, privKey);
			System.out.println(w2);

			assertEquals(w1, w2);
		}

	}

}
