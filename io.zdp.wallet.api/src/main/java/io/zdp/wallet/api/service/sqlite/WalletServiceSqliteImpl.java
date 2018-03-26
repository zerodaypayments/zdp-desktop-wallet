package io.zdp.wallet.api.service.sqlite;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Base58;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;

@Component
public class WalletServiceSqliteImpl implements WalletService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Connection getConnection(File file) {

		Connection c = null;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
			log.debug("Opened database successfully: " + file);
		} catch (Exception e) {
			log.error("Error opening DB file: ", e);
		}

		return c;

	}

	@Override
	public Wallet create(String privKey, File file) throws Exception {

		final Wallet w = new Wallet();
		w.setDateCreated(new Date());

		if (StringUtils.isBlank(privKey)) {
			privKey = Base58.encode(CryptoUtils.generateECPrivateKey().toByteArray());
		}

		w.setUuid(UUID.randomUUID().toString());
		w.setDateLastUpdated(new Date());
		w.setPrivateKey(privKey);
		w.setPublicKey(
				Base58.encode(CryptoUtils.getPublicKeyFromPrivate(new BigInteger(Base58.decode(privKey)), true)));

		save(file, w);

		return w;
	}

	@Override
	public void save(File file, Wallet wallet) throws Exception {

		if (false == file.exists()) {

			log.debug("Create new db file: " + file);

			try (Connection conn = this.getConnection(file); Statement stmt = conn.createStatement();) {

				log.debug("Created new db file: " + file);

				String sql = sql("create-new.sql");

				stmt.executeUpdate(sql);

			}

		}

	}

	@Override
	public Wallet load(File file) throws Exception {

		try (Connection conn = this.getConnection(file); ) {

			QueryRunner qr = new QueryRunner();
			Wallet w = qr.query(conn, sql("load-wallet.sql"), new ResultSetHandler<Wallet> {},null);
			
			return w;
		}

		return null;
		
		
	}

	private String sql(String scriptFilename) throws IOException {
		return IOUtils.toString(this.getClass().getResourceAsStream("/sql/" + scriptFilename), StandardCharsets.UTF_8);
	}
}
