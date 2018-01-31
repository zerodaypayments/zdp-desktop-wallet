package io.zdp.wallet.api.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.zdp.wallet.api.service.WalletService;

@SuppressWarnings("serial")
@XmlRootElement
public class Wallet implements Serializable {

	protected String uuid;

	protected List<WalletAddress> addresses = new ArrayList<>();

	protected Date dateCreated;

	protected String seed;

	public WalletAddress getByPublicKeyHash(String address) {
		for (WalletAddress addr : this.addresses) {
			if (WalletService.getPublicKeyHash(addr).equals(address)) {
				return addr;
			}
		}

		return null;
	}

	public String getSeed() {
		return seed;
	}

	@XmlElement
	public void setSeed(String seed) {
		this.seed = seed;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<WalletAddress> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<WalletAddress> addresses) {
		this.addresses = addresses;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Override
	public String toString() {
		return "Wallet [uuid=" + uuid + ", addresses=" + addresses + ", dateCreated=" + dateCreated + ", seed=" + seed + "]";
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
		Wallet other = (Wallet) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

}
