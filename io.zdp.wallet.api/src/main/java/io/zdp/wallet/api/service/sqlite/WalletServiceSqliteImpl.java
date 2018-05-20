package io.zdp.wallet.api.service.sqlite;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Component;

import io.zdp.wallet.api.domain.AccountTransaction;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.service.WalletService;

@Component
public class WalletServiceSqliteImpl implements WalletService {

	@Override
	public Wallet create(String privKey, File file) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(File file, Wallet wallet) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Wallet load(File file) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveTransaction(File file, AccountTransaction tx) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AccountTransaction getTransactionByUuid(File file, String uuid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countTransactions(File file) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<AccountTransaction> listTransactions(int page, int size) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
/*
	private static final String TX_DATE_FORMAT = "yyyy-MM-dd kk:mm:ss";
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

		if (StringUtils.isBlank(privKey)) {
			privKey = Base58.encode(CryptoUtils.generateECPrivateKey().toByteArray());
		}

		w.setVersion(1);
		w.setUuid(UUID.randomUUID().toString());
		w.setPrivateKey(privKey);
		w.setPublicKey(Base58.encode(CryptoUtils.getPublicKeyFromPrivate(new BigInteger(Base58.decode(privKey)), true)));

		save(file, w);

		return w;
	}

	@Override
	public void save(File file, Wallet wallet) throws Exception {

		if (false == file.exists()) {

			log.debug("Create new db file: " + file);

			try (Connection conn = this.getConnection(file); Statement createTableStatement = conn.createStatement(); Statement insertNewWalletStatement = conn.createStatement();) {

				log.debug("Created new db file: " + file);

				{
					String sql = sql("create-new.sql");
					createTableStatement.executeUpdate(sql);
				}

				log.debug("Saving new wallet: " + wallet);

				{
					String sql = sql("save-new-wallet.sql");
					sql = StringUtils.replace(sql, "{id}", "1");

					sql = StringUtils.replace(sql, "{uuid}", wallet.getUuid());
					sql = StringUtils.replace(sql, "{version}", Integer.toString(wallet.getVersion()));
					sql = StringUtils.replace(sql, "{private_key}", wallet.getPrivateKey());
					sql = StringUtils.replace(sql, "{public_key}", wallet.getPublicKey());
					sql = StringUtils.replace(sql, "{balance}", wallet.getBalance().toPlainString());

					log.debug("Sql: " + sql);

					insertNewWalletStatement.executeUpdate(sql);
				}

			}

		}

		// save to file

	}

	@Override
	public Wallet load(File file) throws Exception {

		log.debug("Loaded from wallet: " + file);

		try (Connection conn = this.getConnection(file);) {

			QueryRunner qr = new QueryRunner();
			Wallet w = qr.query(conn, sql("load-wallet.sql"), new ResultSetHandler<Wallet>() {

				@Override
				public Wallet handle(ResultSet rs) throws SQLException {

					Wallet w = new Wallet();
					w.setUuid(rs.getString("uuid"));
					w.setVersion(rs.getInt("version"));
					w.setPrivateKey(rs.getString("private_key"));
					w.setPublicKey(rs.getString("public_key"));
					w.setBalance(rs.getBigDecimal("balance"));

					log.debug("Loaded wallet: " + w);

					return w;
				}
			});

			return w;
		}

	}

	private String sql(String scriptFilename) throws IOException {
		return IOUtils.toString(this.getClass().getResourceAsStream("/sql/" + scriptFilename), StandardCharsets.UTF_8);
	}

	@Override
	public void saveTransaction(File file, AccountTransaction tx) throws Exception {

		if (file.exists()) {

			log.debug("save new tx: " + tx);

			try (Connection conn = this.getConnection(file); Statement stmt = conn.createStatement();) {

				{
					String sql = sql("save-new-tx.sql");

					sql = StringUtils.replace(sql, "{uuid}", tx.getUuid());

					sql = StringUtils.replace(sql, "{amount}", tx.getAmount().toPlainString());

					if (tx.getFee() != null) {
						sql = StringUtils.replace(sql, "{fee}", tx.getFee().toPlainString());
					}

					sql = StringUtils.replace(sql, "{date}", new SimpleDateFormat(TX_DATE_FORMAT).format(tx.getDate()));

					sql = StringUtils.replace(sql, "{from_address}", tx.getFrom());

					sql = StringUtils.replace(sql, "{to_address}", tx.getTo());

					sql = StringUtils.replace(sql, "{memo}", tx.getMemo());

					log.debug("Sql: " + sql);

					stmt.executeUpdate(sql);
				}

			}

		}

	}

	@Override
	public AccountTransaction getTransactionByUuid(File file, String uuid) throws Exception {

		log.debug("getTransactionByUuid from wallet: " + file + " by '" + uuid + "'");

		try (Connection conn = this.getConnection(file);) {

			QueryRunner qr = new QueryRunner();

			String sql = sql("get-tx-by-uuid.sql");
			sql = StringUtils.replace(sql, "{uuid}", uuid);

			log.debug("Sql: " + sql);

			AccountTransaction tx = qr.query(conn, sql, new ResultSetHandler<AccountTransaction>() {

				@Override
				public AccountTransaction handle(ResultSet rs) throws SQLException {

					AccountTransaction tx = new AccountTransaction();
					tx.setAmount(rs.getBigDecimal("amount"));
					try {
						tx.setDate(new SimpleDateFormat(TX_DATE_FORMAT).parse(rs.getString("date")));
					} catch (ParseException e) {
						log.error("Error: ", e);
					}
					tx.setFee(rs.getBigDecimal("fee"));
					tx.setFrom(rs.getString("from_address"));
					tx.setMemo(rs.getString("memo"));
					tx.setTo(rs.getString("to_address"));
					tx.setUuid(rs.getString("uuid"));

					log.debug("Loaded tx: " + tx);

					return tx;
				}
			});

			return tx;
		}

	}

	@Override
	public long countTransactions(File file) throws Exception {

		log.debug("count transactions from wallet: " + file);

		try (Connection conn = this.getConnection(file);) {

			QueryRunner qr = new QueryRunner();

			String sql = sql("count-tx.sql");

			log.debug("Sql: " + sql);

			Long count = qr.query(conn, sql, new ResultSetHandler<Long>() {

				@Override
				public Long handle(ResultSet rs) throws SQLException {
					return rs.getLong(1);
				}
			});

			return count;
		}

	}

	@Override
	public List<AccountTransaction> listTransactions(int page, int size) throws Exception {
		
		
		
		return null;
		
	}
	*/
}