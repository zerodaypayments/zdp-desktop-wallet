package io.zdp.wallet.api.service.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class WalletServiceJsonFile {

	private static final Logger log = LoggerFactory.getLogger(WalletServiceJsonFile.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	/*
	public static Wallet create(String privKey, File file) throws Exception {

		final Wallet w = new Wallet();

		if (StringUtils.isBlank(privKey)) {
			privKey = Base58.encode(CryptoUtils.generateECPrivateKey().toByteArray());
		}

		w.setUuid(UUID.randomUUID().toString());
		w.setPrivateKey(privKey);
		w.setPublicKey(Base58.encode(CryptoUtils.getPublicKeyFromPrivate(new BigInteger(Base58.decode(privKey)), true)));

		save(file, w);

		return w;
	}

	// Save wallet in JSON format
	public static synchronized void save(final File file, final Wallet wallet) throws Exception {
		mapper.writeValue(file, wallet);
		log.debug("Wallet [" + wallet.getUuid() + "]  saved to: " + file);
	}

	// Read wallet from a JSON file
	public static synchronized Wallet load(final File file) throws Exception {
		return mapper.readValue(file, Wallet.class);
	}
	*/
}
