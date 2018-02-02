package io.zdp.wallet.api.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WalletTransaction implements Serializable {

	private String uuid;

	private BigDecimal amount;

	private BigDecimal fee;

	private Date date;

	private String from;

	private String to;

	private String senderRef;

	private String recepientRef;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSenderRef() {
		return senderRef;
	}

	public void setSenderRef(String senderRef) {
		this.senderRef = senderRef;
	}

	public String getRecepientRef() {
		return recepientRef;
	}

	public void setRecepientRef(String recepientRef) {
		this.recepientRef = recepientRef;
	}

	@Override
	public String toString() {
		return "WalletTransaction [uuid=" + uuid + ", amount=" + amount + ", fee=" + fee + ", date=" + date + ", from=" + from + ", to=" + to + ", senderRef=" + senderRef + ", recepientRef=" + recepientRef + "]";
	}

}
