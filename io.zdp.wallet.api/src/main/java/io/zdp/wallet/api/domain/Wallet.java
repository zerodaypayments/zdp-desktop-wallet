package io.zdp.wallet.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@SuppressWarnings("serial")
@XmlRootElement
public class Wallet implements Serializable {

	protected String uuid;

	protected List<WalletTransaction> transactions = new ArrayList<>();

	protected String privateKey;

	protected String publicKey;

	protected int version;

	protected BigDecimal balance = BigDecimal.ZERO;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
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

	public WalletTransaction getTxByUuid(String uuid) {
		for (WalletTransaction tx : transactions) {
			if (tx.getUuid().equals(uuid)) {
				return tx;
			}
		}
		return null;
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
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result + ((privateKey == null) ? 0 : privateKey.hashCode());
		result = prime * result + ((publicKey == null) ? 0 : publicKey.hashCode());
		result = prime * result + ((transactions == null) ? 0 : transactions.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wallet other = (Wallet) obj;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (privateKey == null) {
			if (other.privateKey != null)
				return false;
		} else if (!privateKey.equals(other.privateKey))
			return false;
		if (publicKey == null) {
			if (other.publicKey != null)
				return false;
		} else if (!publicKey.equals(other.publicKey))
			return false;
		if (transactions == null) {
			if (other.transactions != null)
				return false;
		} else if (!transactions.equals(other.transactions))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
