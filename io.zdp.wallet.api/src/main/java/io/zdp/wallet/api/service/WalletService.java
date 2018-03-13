package io.zdp.wallet.api.service;

import java.io.File;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Base58;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.api.domain.Wallet;

public class WalletService {

	private static final Logger log = LoggerFactory.getLogger(WalletService.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public static Wallet create(String privKey, File file) throws Exception {

		final Wallet w = new Wallet();
		w.setDateCreated(new Date());

		if (StringUtils.isBlank(privKey)) {
			privKey = Base58.encode(CryptoUtils.generateECPrivateKey().toByteArray());
		}

		w.setUuid(UUID.randomUUID().toString());
		w.setDateLastUpdated(new Date());
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
}
