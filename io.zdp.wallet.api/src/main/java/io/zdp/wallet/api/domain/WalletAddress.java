package io.zdp.wallet.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.codec.digest.DigestUtils;

import io.zdp.wallet.api.service.WalletService;

@SuppressWarnings("serial")
@XmlRootElement
public class WalletAddress implements Serializable {

	private byte[] privateKey;

	private byte[] publicKey;

	private BigDecimal balance;

	public BigDecimal getBalance() {
		return balance;
	}

	@XmlElement
	public void setBalance(BigDecimal balance) {
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

	public String getAddress() {
		return WalletService.getPublicKeyHash(this);
	}

	@Override
	public String toString() {
		return "WalletAddress [privateKey=" + DigestUtils.sha512Hex(privateKey) + ", address=" + getAddress() + ", balance=" + balance + "]";
	}

}
