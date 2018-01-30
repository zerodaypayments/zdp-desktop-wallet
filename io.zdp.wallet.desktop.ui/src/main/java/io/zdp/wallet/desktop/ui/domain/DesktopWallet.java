package io.zdp.wallet.desktop.ui.domain;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.zdp.wallet.api.domain.Wallet;
import io.zdp.wallet.api.domain.WalletAddress;
import io.zdp.wallet.api.service.WalletService;

@SuppressWarnings("serial")
public class DesktopWallet extends Wallet {

	private List<RecepientAddress> recepientAddresses = new ArrayList<>();

	private List<WalletEvent> walletEvents = new ArrayList<>();

	private String name;

	@JsonIgnore
	private File file;

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

	@JsonIgnore
	public String getDateAsString() {
		return new SimpleDateFormat("dd MMM yyyy hh:mm a").format(dateCreated);
	}

	public WalletAddress getMyAddressByUuid(String uuid) {
		for (WalletAddress addr : this.addresses) {
			if (WalletService.getPublicKeyHash(addr).equals(uuid)) {
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

}
