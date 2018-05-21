package io.zdp.wallet.api.db.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "account")
public class Account implements Serializable {

	@Id
	@Column(name = "ID", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "PRIV_KEY", nullable = false, unique = true)
	private String privateKey;

	@Column(name = "PUB_KEY", nullable = false)
	private String publicKey;

	@Column(name = "ZDP_UUID", nullable = false)
	private String zdpUuid;

	@Column(name = "UUID", columnDefinition = "BINARY(20)", nullable = false, updatable = false, unique = true)
	private byte[] uuid;

	@Column(name = "BALANCE", nullable = false)
	private BigDecimal balance = BigDecimal.ZERO;

	@Column(name = "BLOCK_HEIGHT", nullable = false)
	private long height;

	@Column(name = "CURVE", columnDefinition = "SMALLINT UNSIGNED", nullable = false)
	private int curve;

	@Column(name = "HASH", columnDefinition = "BINARY(20)", nullable = false)
	private byte[] transferChainHash;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "WALLET_ID")
	private Wallet wallet;

	@OneToMany(fetch = FetchType.LAZY)
	protected List<AccountTransaction> transactions;

	public String getZdpUuid() {
		return zdpUuid;
	}

	public void setZdpUuid(String zdpUuid) {
		this.zdpUuid = zdpUuid;
	}

	public Wallet getWallet() {
		return wallet;
	}

	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
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

	public byte[] getUuid() {
		return uuid;
	}

	public void setUuid(byte[] uuid) {
		this.uuid = uuid;
	}

	public long getId() {
		return id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public long getHeight() {
		return height;
	}

	public void setHeight(long height) {
		this.height = height;
	}

	public int getCurve() {
		return curve;
	}

	public void setCurve(int curve) {
		this.curve = curve;
	}

	public byte[] getTransferChainHash() {
		return transferChainHash;
	}

	public void setTransferChainHash(byte[] transferChainHash) {
		this.transferChainHash = transferChainHash;
	}

	public List<AccountTransaction> getTransactions() {
		return transactions;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", privateKey=" + privateKey + ", publicKey=" + publicKey + ", zdpUuid=" + zdpUuid + ", uuid=" + Arrays.toString(uuid) + ", balance=" + balance + ", height=" + height + ", curve=" + curve + ", transferChainHash=" + Arrays.toString(transferChainHash) + ", wallet=" + wallet + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		Account other = (Account) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

}
