package io.zdp.wallet.desktop.api.domain;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public class Wallet implements Serializable {

	private String uuid;

	private List<MyAddress> myAddresses = new ArrayList<>();

	private List<RecepientAddress> recepientAddresses = new ArrayList<>();

	private List<WalletEvent> walletEvents = new ArrayList<>();

	private Date dateCreated;

	private String name;

	@JsonIgnore
	private File file;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<WalletEvent> getWalletEvents() {
		return walletEvents;
	}

	public void setWalletEvents(List<WalletEvent> walletEvents) {
		this.walletEvents = walletEvents;
	}

	public List<RecepientAddress> getRecepientAddresses() {
		return recepientAddresses;
	}

	public void setRecepientAddresses(List<RecepientAddress> recepientAddresses) {
		this.recepientAddresses = recepientAddresses;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MyAddress> getMyAddresses() {
		return myAddresses;
	}

	public void setMyAddresses(List<MyAddress> myAddresses) {
		this.myAddresses = myAddresses;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@JsonIgnore
	public String getDateAsString() {
		return new SimpleDateFormat("dd MMM yyyy hh:mm a").format(dateCreated);
	}

	public MyAddress getMyAddressByUuid(String uuid) {
		for (MyAddress addr : this.myAddresses) {
			if (addr.getAddress().equals(uuid)) {
				return addr;
			}
		}
		return null;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateCreated == null) ? 0 : dateCreated.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Wallet other = (Wallet) obj;
		if (dateCreated == null) {
			if (other.dateCreated != null)
				return false;
		} else if (!dateCreated.equals(other.dateCreated))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

}
