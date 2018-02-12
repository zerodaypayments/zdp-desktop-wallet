package io.zdp.wallet.desktop.api.service;

import org.junit.Test;

import io.zdp.wallet.api.ZdpCli;
import junit.framework.TestCase;

public class TestZdpCli extends TestCase {

	@Test
	public void test() throws Exception {

		ZdpCli cli = new ZdpCli();

		cli.main(new String[] { "--createwallet", "--pass", "123", "--file", "1.wallet" });
	}

}
