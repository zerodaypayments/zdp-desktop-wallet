package io.zdp.wallet.api.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement
public class RecepientAddress implements Serializable {

	private String address;

	private String description;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "RecepientAddress [address=" + address + ", description=" + description + "]";
	}

}
