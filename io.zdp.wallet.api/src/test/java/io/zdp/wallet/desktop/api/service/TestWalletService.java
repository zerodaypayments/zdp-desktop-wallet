	package io.zdp.wallet.desktop.api.service;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.wallet.api.db.domain.Wallet;
import io.zdp.wallet.api.service.ApiService;
import junit.framework.TestCase;

public class TestWalletService extends TestCase {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
/*
	@Test
	public void test() throws Exception {

		File file = new File(SystemUtils.getJavaIoTmpDir(), "1.wallet");

		Wallet w1 = WalletService.create(null, file);

		assertNotNull(w1);
		assertFalse(w1.getPrivateKey().isEmpty());
		assertFalse(w1.getPublicKey().isEmpty());
		assertFalse(w1.getUuid().isEmpty());

		System.out.println(w1);

		{
			Wallet w2 = WalletService.load(file);
			System.out.println(w2);

			assertEquals(w1, w2);
		}

	}
	*/

	@Test
	public void test() throws Exception {

		
	}
	
}
