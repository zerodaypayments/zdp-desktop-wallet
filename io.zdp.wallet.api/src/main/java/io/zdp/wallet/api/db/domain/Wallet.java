package io.zdp.wallet.api.db.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "wallet")
public class Wallet implements Serializable {

	@Id
	@Column(name = "ID", nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id = 1;

	@Column(name = "UUID", nullable = false)
	protected String uuid;

	@Column(name = "DATE", nullable = false)
	protected Date date;

	@Column(name = "VERSION", nullable = false)
	protected String version;

	@OneToMany(fetch = FetchType.EAGER)
	protected List<Account> accounts;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Account> getAccounts() {

		if (accounts == null) {
			accounts = new ArrayList<>();
		}

		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	@Override
	public String toString() {
		return "Wallet [id=" + id + ", uuid=" + uuid + ", date=" + date + ", version=" + version + "]";
	}

}
