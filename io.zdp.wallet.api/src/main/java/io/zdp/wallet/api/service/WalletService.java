package io.zdp.wallet.api.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
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
import io.zdp.common.utils.ZIPHelper;
import io.zdp.wallet.api.domain.Wallet;

public class WalletService {

	private static final Logger log = LoggerFactory.getLogger(WalletService.class);

	public static Wallet create(String privKey, File file, String pass) throws Exception {

		final Wallet w = new Wallet();
		w.setDateCreated(new Date());

		if (StringUtils.isBlank(privKey)) {
			privKey = CryptoUtils.generateRandomNumber256bits();
		}

		w.setUuid(DigestUtils.sha256Hex(privKey));

		w.setSeed(privKey);

		save(file, w, pass);

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
	public static synchronized void save(final File file, final Wallet wallet, final String password) {

		try {

			// Generate XML
			final StringWriter sw = new StringWriter();
			getWalletMarshaller().marshal(wallet, sw);

			// Compress XML into byte array
			final byte[] compressed = ZIPHelper.compress(sw.toString());

			final byte[] encrypted = CryptoUtils.encryptLargeData(password, compressed);

			FileUtils.writeByteArrayToFile(file, encrypted);

		} catch (Exception e) {
			log.error("Error: ", e);
		}

	}

	public static synchronized Wallet load(final File file, final String pass) {

		try {

			// Read decrypted file content to byte array
			final byte[] content = FileUtils.readFileToByteArray(file);

			final byte[] decryptedCompressed = CryptoUtils.decryptLargeData(pass, content);

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
