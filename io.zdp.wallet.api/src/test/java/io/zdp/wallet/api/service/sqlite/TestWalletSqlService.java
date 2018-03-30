package io.zdp.wallet.api.service.sqlite;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.domain.WalletTransaction;
import junit.framework.TestCase;

public class TestWalletSqlService extends TestCase {

	@Test
	public void test() throws Exception {

		System.out.println("start");

		WalletServiceSqliteImpl service = new WalletServiceSqliteImpl();

		File file = new File(SystemUtils.getJavaIoTmpDir(), UUID.randomUUID().toString());

		Wallet original = service.create(CryptoUtils.getNewAccount().getPrivateKey58(), file);

		assertNotNull(original);

		System.out.println(original);

		// load wallet now and compare
		Wallet loaded = service.load(file);

		assertEquals(original, loaded);

		// save a new transaction
		int i=0;
		while (true) {
			
			long st=System.currentTimeMillis();
			
			WalletTransaction tx = new WalletTransaction();
			tx.setAmount(BigDecimal.valueOf(RandomUtils.nextInt()));
			tx.setDate(new Date());
			tx.setFee(BigDecimal.valueOf(RandomUtils.nextInt()));
			tx.setFrom("From 1");
			tx.setMemo("Memo 1");
			tx.setTo("To 1");
			tx.setUuid(UUID.randomUUID().toString());

			service.saveTransaction(file, tx);

			long et=System.currentTimeMillis();
			System.out.println("Save tx took " + (et - st) + " ms.");
			
			i++;
			
			// get by uuid
			long st1=System.currentTimeMillis();
			WalletTransaction loadedTx = service.getTransactionByUuid(file, tx.getUuid());
			long et1=System.currentTimeMillis();
			System.out.println("Load tx took " + (et1 - st1) + " ms.");

			assertNotNull(loadedTx);
			
			assertEquals(tx, loadedTx);
			
			if (i%100==0) {
				System.out.println("Tx saved: " + i);
			}

		}


	}
}
