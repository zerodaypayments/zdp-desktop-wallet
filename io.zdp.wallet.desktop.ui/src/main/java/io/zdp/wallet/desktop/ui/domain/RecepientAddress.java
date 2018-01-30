package io.zdp.wallet.desktop.ui.domain;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class RecepientAddress implements Serializable {

	private String address;

	private Date date;

	private String description;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "RecepientAddress [address=" + address + ", date=" + date + ", description=" + description + "]";
	}

}
