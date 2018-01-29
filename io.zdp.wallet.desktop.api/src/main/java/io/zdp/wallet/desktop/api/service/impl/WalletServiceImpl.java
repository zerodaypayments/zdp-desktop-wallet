package io.zdp.wallet.desktop.api.service.impl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.desktop.api.domain.Wallet;
import io.zdp.wallet.desktop.api.domain.WalletEvent;
import io.zdp.wallet.desktop.api.service.WalletService;

@Service
public class WalletServiceImpl implements WalletService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public Wallet create(File file, String name, char[] password) {

		final Wallet w = new Wallet();
		w.setName(name);
		w.setDateCreated(new Date());
		w.setUuid(UUID.randomUUID().toString());

		final WalletEvent e = new WalletEvent();
		e.setDate(new Date());
		e.setType(WalletEvent.WALLET_CREATED);
		w.getWalletEvents().add(e);

		w.setFile(file);
		save(w, password);

		return w;
	}

	@Override
	public Wallet load(File file, char[] pass) {

		try {

			final String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

			// decrypt
			final String json = CryptoUtils.decrypt(content, pass);

			final Wallet w = new ObjectMapper().readValue(json, Wallet.class);

			w.setFile(file);

			return w;

		} catch (Exception e) {
			log.error("Error: ", e);
		}

		return null;

	}

	@Override
	public void save(Wallet wallet, char[] pass) {

		try {

			// to json
			String json = new ObjectMapper().writeValueAsString(wallet);

			// encrypt
			String enc = CryptoUtils.encrypt(json, pass);

			// write to file

			FileUtils.writeStringToFile(wallet.getFile(), enc, StandardCharsets.UTF_8);

		} catch (Exception e) {
			log.error("Error: ", e);
		}

	}

}
