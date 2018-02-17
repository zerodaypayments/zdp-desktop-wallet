package io.zdp.wallet.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import io.zdp.common.crypto.CryptoUtils;

@SuppressWarnings("serial")
@XmlRootElement
public class Wallet implements Serializable {

	protected String uuid;

	protected List<WalletTransaction> transactions = new ArrayList<>();

	protected Date dateCreated;

	protected String seed;

	protected BigDecimal balance = BigDecimal.ZERO;

	protected transient KeyPair keys;

	public KeyPair getKeys() {

		if (keys == null) {
			try {
				keys = CryptoUtils.generateKeys(seed);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return keys;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<WalletTransaction> getTransactions() {
		Collections.sort(transactions, new Comparator<WalletTransaction>() {
			@Override
			public int compare(WalletTransaction o1, WalletTransaction o2) {
				return o2.getDate().compareTo(o1.getDate());
			}
		});
		return transactions;
	}

	public void setTransactions(List<WalletTransaction> transactions) {
		this.transactions = transactions;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	@Override
	public String toString() {
		return "Wallet [uuid=" + uuid + ", transactions=" + transactions + ", dateCreated=" + dateCreated + ", seed=" + seed + ", balance=" + balance + "]";
	}

	public byte[] getPublicKey() {
		return getKeys().getPublic().getEncoded();
	}

	public byte[] getPrivateKey() {
		return getKeys().getPrivate().getEncoded();
	}

}
