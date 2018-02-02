package io.zdp.wallet.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.zdp.common.crypto.CryptoUtils;
import io.zdp.wallet.api.service.WalletService;

@SuppressWarnings("serial")
@XmlRootElement
public class WalletAddress implements Serializable {

	private transient byte[] privateKey;

	private transient byte[] publicKey;

	private String seed;

	private BigDecimal balance;

	public BigDecimal getBalance() {
		return balance;
	}

	@XmlElement
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public byte[] getPrivateKey() {
		if (privateKey == null) {
			initKeys();
		}
		return privateKey;
	}

	public byte[] getPublicKey() {
		if (publicKey == null) {
			initKeys();
		}
		return publicKey;
	}

	private void initKeys() {
		try {
			KeyPair keys = CryptoUtils.generateKeys(seed);
			this.privateKey = keys.getPrivate().getEncoded();
			this.publicKey = keys.getPublic().getEncoded();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getAddress() {
		return WalletService.getPublicKeyHash(this);
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "WalletAddress [privateKey=" + Arrays.toString(privateKey) + ", publicKey=" + Arrays.toString(publicKey) + ", seed=" + seed + ", balance=" + balance + "]";
	}

}
