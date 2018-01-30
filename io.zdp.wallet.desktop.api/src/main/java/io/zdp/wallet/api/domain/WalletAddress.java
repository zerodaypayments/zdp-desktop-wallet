package io.zdp.wallet.api.domain;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.commons.codec.digest.DigestUtils;

import io.zdp.wallet.api.service.WalletService;

@SuppressWarnings("serial")
public class WalletAddress implements Serializable {

	private byte[] privateKey;

	private byte[] publicKey;

	private BigInteger balance;

	public BigInteger getBalance() {
		return balance;
	}

	public void setBalance(BigInteger balance) {
		this.balance = balance;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}

	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public String toString() {
		return "WalletAddress [privateKey=" + DigestUtils.sha512Hex(privateKey) + ", address=" + WalletService.getPublicKeyHash(this) + ", balance=" + balance + "]";
	}

}
