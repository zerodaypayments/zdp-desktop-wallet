package io.zdp.wallet.api.service.sqlite;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;

import junit.framework.TestCase;

public class TestWalletSqlServiceTx extends TestCase {

	@Test
	public void test() throws Exception {

		WalletServiceSqliteImpl service = new WalletServiceSqliteImpl();

		File file = new File(SystemUtils.getJavaIoTmpDir(), "9dbf8976-f483-495a-b6ad-98a3009e778f");

		long count = service.countTransactions(file);

		System.out.println(count);

	}
}
