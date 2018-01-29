package io.zdp.wallet.desktop.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.zdp.wallet.desktop.api.domain.Wallet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/test-spring-context.xml")
public class TestWalletService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private WalletService walletService;

	@Test
	public void test() {

		File file = new File(SystemUtils.getJavaIoTmpDir(), UUID.randomUUID().toString());

		assertFalse(file.exists());

		Wallet w1 = walletService.create(file, "my new wallet", "pass123".toCharArray());

		assertTrue(file.exists());

		Wallet w2 = walletService.load(file, "pass123".toCharArray());

		assertNotNull(w2);

		assertEquals(w1, w2);

	}

}
