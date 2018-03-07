package io.zdp.wallet.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement
public class Wallet implements Serializable {

	protected String uuid;

	protected List<WalletTransaction> transactions = new ArrayList<>();

	protected Date dateCreated;

	protected Date dateLastUpdated;

	protected String privateKey;

	protected String publicKey;

	protected BigDecimal balance = BigDecimal.ZERO;

	public Date getDateLastUpdated() {
		return dateLastUpdated;
	}

	public void setDateLastUpdated(Date dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
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

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public String toString() {
		return "Wallet [uuid=" + uuid + ", transactions=" + transactions + ", dateCreated=" + dateCreated + ", privateKey=" + privateKey + ", publicKey=" + publicKey + ", balance=" + balance + "]";
	}

}
