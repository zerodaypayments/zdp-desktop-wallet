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

	// Save Wallet in XML format
	// Switching to XML from JSON as easier to support changes in the wallet
	// format
	// in the future
	public static synchronized void save(final File file, final Wallet wallet, final String walletSeed) {

		try {

			// Generate XML
			final StringWriter sw = new StringWriter();
			getWalletMarshaller().marshal(wallet, sw);

			// TODO remove debugging logging
			System.out.println(sw);

			// Compress XML into byte array
			final byte[] compressed = ZIPHelper.compress(sw.toString());

			final byte[] encrypted = CryptoUtils.encryptLargeData(walletSeed, compressed);

			FileUtils.writeByteArrayToFile(file, encrypted);

		} catch (Exception e) {
			log.error("Error: ", e);
		}

	}

	public static synchronized Wallet load(final File file, final String walletSeed) {

		try {

			// Read decrypted file content to byte array
			final byte[] content = FileUtils.readFileToByteArray(file);

			final byte[] decryptedCompressed = CryptoUtils.decryptLargeData(walletSeed, content);

			final byte[] xml = ZIPHelper.decompressAsBytes(decryptedCompressed);

			// TODO remove debugging logging
			System.out.println(new String(xml));

			final Wallet wallet = (Wallet) getWalletUnmarshaller().unmarshal(new ByteArrayInputStream(xml));

			return wallet;

		} catch (Exception e) {
			log.error("Error: ", e);
		}

		return null;

	}
}
