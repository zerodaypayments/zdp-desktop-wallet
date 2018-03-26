package io.zdp.wallet.api.service.sqlite;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.api.domain.Wallet;
import junit.framework.TestCase;

public class TestWalletSqlService extends TestCase {
	
	@Test
	public void test() throws Exception {
		
		System.out.println("start");
		
		WalletServiceSqliteImpl service = new WalletServiceSqliteImpl();
		
		Wallet w = service.create(CryptoUtils.getNewAccount().getLeft(), new File(SystemUtils.getJavaIoTmpDir(), UUID.randomUUID().toString()));
		
		assertNotNull(w);
		
	}
}
