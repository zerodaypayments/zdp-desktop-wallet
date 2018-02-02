package io.zdp.wallet.api.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.common.crypto.Signer;
import io.zdp.common.utils.ZIPHelper;
import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.domain.WalletAddress;

public class WalletService {

	private static final Logger log = LoggerFactory.getLogger(WalletService.class);

	public static Wallet create(String privKey, File file) throws Exception {

		final Wallet w = new Wallet();
		w.setDateCreated(new Date());

		if (StringUtils.isBlank(privKey)) {
			privKey = CryptoUtils.generateRandomNumber256bits();
		}

		w.setUuid(DigestUtils.sha256Hex(privKey));

		w.setSeed(privKey);

		save(file, w, privKey);

		return w;
	}

	public static synchronized WalletAddress getNewAddress(File file, Wallet wallet, String pass) throws Exception {

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

	public static String getPublicKeyHash(final WalletAddress addr) {
		return Signer.getPublicKeyHash(addr.getPublicKey());
	}

	// Save Wallet in XML format
	// Switching to XML from JSON as easier to support changes in the wallet
	// format
	// in the future
	public static synchronized void save(final File file, final Wallet wallet, final String walletSeed) {

		try {

			// Generate XML
			final StringWriter sw = new StringWriter();
			getWalletMarshaller().marshal(wallet, sw);

			// Compress XML into byte array
			final byte[] compressed = ZIPHelper.compress(sw.toString());

			// RSA-encrypt compressed byte array
			final KeyPair keys = CryptoUtils.generateKeys(walletSeed);

			final byte[] encrypted = CryptoUtils.encrypt(keys.getPrivate(), compressed);

			FileUtils.writeByteArrayToFile(file, encrypted);

		} catch (Exception e) {
			log.error("Error: ", e);
		}

	}

	private static Marshaller getWalletMarshaller() throws JAXBException, PropertyException {
		final JAXBContext jaxbContext = JAXBContext.newInstance(Wallet.class);
		final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		return jaxbMarshaller;
	}

	private static Unmarshaller getWalletUnmarshaller() throws JAXBException, PropertyException {
		final JAXBContext jaxbContext = JAXBContext.newInstance(Wallet.class);
		final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return jaxbUnmarshaller;
	}

	public static synchronized Wallet load(final File file, final String walletSeed) {

		try {

			// Read decrypted file content to byte array
			final byte[] content = FileUtils.readFileToByteArray(file);

			// Decrypt content to compressed byte array
			final KeyPair keys = CryptoUtils.generateKeys(walletSeed);

			final byte[] decryptedCompressed = CryptoUtils.decrypt(keys.getPublic(), content);

			final byte[] xml = ZIPHelper.decompressAsBytes(decryptedCompressed);

			final Wallet wallet = (Wallet) getWalletUnmarshaller().unmarshal(new ByteArrayInputStream(xml));

			return wallet;

		} catch (Exception e) {
			log.error("Error: ", e);
		}

		return null;

	}
}
