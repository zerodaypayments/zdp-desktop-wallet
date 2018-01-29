package io.zdp.wallet.desktop.api.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.zdp.common.utils.StringHelper;

@SuppressWarnings("serial")
public class MyAddress implements Serializable {

	private byte[] privateKey;

	private String address;

	private String seed;

	private String description;

	private double balance;

	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getBalance() {
		return balance;
	}

	@JsonIgnore
	public String getBalanceGrouped() {
		return StringHelper.format(balance);
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(byte[] privateKey) {
		this.privateKey = privateKey;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

}
