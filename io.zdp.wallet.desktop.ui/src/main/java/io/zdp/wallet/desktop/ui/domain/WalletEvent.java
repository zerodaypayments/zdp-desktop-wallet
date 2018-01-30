package io.zdp.wallet.desktop.ui.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("serial")
public class WalletEvent implements Serializable {

	public static final int NO_TYPE = -1;

	public static final int WALLET_CREATED = 0;
	public static final int ADDRESS_GENERATED = 1;

	private int type;

	private Date date;

	private String message;

	public int getType() {
		return type;
	}

	@JsonIgnore
	public String getDateAsString() {
		return new SimpleDateFormat("dd MMM yy hh:mm a").format(date);
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	@JsonIgnore
	public String getRichMessage() {
		if (type == WALLET_CREATED) {
			return "Wallet created";
		} else if (type == ADDRESS_GENERATED) {
			return "New address generated: " + message;
		} else {
			return "";
		}
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "WalletEvent [type=" + type + ", date=" + date + ", message=" + message + "]";
	}

}
