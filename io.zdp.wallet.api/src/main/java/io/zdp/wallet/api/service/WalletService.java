package io.zdp.wallet.api.service;

import java.io.File;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Base58;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.common.utils.ZIPHelper;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.domain.WalletAddress;

public class WalletService {

	private static final Logger log = LoggerFactory.getLogger(WalletService.class);

	public static Wallet create(String privKey, File file) throws Exception {

		final Wallet w = new Wallet();
		w.setDateCreated(new Date());

		if (StringUtils.isBlank(privKey)) {
			privKey = CryptoUtils.generateRandomNumber(256);
		}

		w.setUuid(DigestUtils.sha256Hex(privKey));

		w.setSeed(privKey);

		save(file, w, privKey.toCharArray());

		return w;
	}

	public static synchronized WalletAddress getNewAddress(File file, Wallet wallet, char[] pass) throws Exception {

		WalletAddress addr = new WalletAddress();

		String seed = null;

		if (wallet.getAddresses().isEmpty()) {
			seed = DigestUtils.sha512Hex(wallet.getSeed());
		} else {
			seed = DigestUtils.sha512Hex(wallet.getAddresses().get(wallet.getAddresses().size() - 1).getPrivateKey());
		}

		KeyPair keys = CryptoUtils.generateKeys(seed);

		addr.setBalance(BigDecimal.ZERO);

		addr.setPrivateKey(keys.getPrivate().getEncoded());
		addr.setPublicKey(keys.getPublic().getEncoded());

		wallet.getAddresses().add(addr);
		save(file, wallet, pass);

		return addr;

	}

	public static String getPublicKeyHash(WalletAddress addr) {

		byte[] addressHash = DigestUtils.sha512(addr.getPublicKey());

		String addressBase58 = Base58.encode(addressHash);

		return addressBase58;

	}

	public static synchronized void save(File file, Wallet wallet, char[] pass) {

		try {

			// to json
			String json = new ObjectMapper().writeValueAsString(wallet);

			// encrypt
			String enc = CryptoUtils.encrypt(json, pass);

			// write to file

			FileUtils.writeByteArrayToFile(file, ZIPHelper.compress(enc));

		} catch (Exception e) {
			log.error("Error: ", e);
		}

	}

	public static synchronized Wallet load(File file, char[] pass) {

		try {

			final String content = ZIPHelper.decompress(FileUtils.readFileToByteArray(file));

			// decrypt
			final String json = CryptoUtils.decrypt(content, pass);

			final Wallet w = new ObjectMapper().readValue(json, Wallet.class);

			return w;

		} catch (Exception e) {
			log.error("Error: ", e);
		}

		return null;

	}

}
