package io.zdp.wallet.desktop.api.service;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.domain.WalletAddress;
import io.zdp.wallet.api.service.WalletService;
import junit.framework.TestCase;

public class TestWalletService extends TestCase {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Test
	public void test() throws Exception {

		File file = new File("1.wallet");

		char[] pass = "pass123".toCharArray();

		String privKey = CryptoUtils.generateRandomNumber(256);
		
		System.out.println(privKey);

		Wallet w1 = WalletService.create(privKey, file, pass);

		assertNotNull(w1);
		assertTrue(w1.getAddresses().isEmpty());
		assertFalse(w1.getSeed().isEmpty());
		assertFalse(w1.getUuid().isEmpty());

		System.out.println(w1);

		{
			Wallet w2 = WalletService.load(file, pass);
			System.out.println(w2);

			assertEquals(w1, w2);
		}

		{
			for (int i = 0; i < 10000; i++) {
				WalletAddress addr1 = WalletService.getNewAddress(file, w1, pass);
				System.out.println(addr1);
			}
		}

	}

}
