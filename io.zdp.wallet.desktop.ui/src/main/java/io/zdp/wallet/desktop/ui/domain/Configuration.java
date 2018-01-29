package io.zdp.wallet.desktop.ui.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Configuration implements Serializable {

	private String lastWalletFile;

	private List<String> recentWallets = new ArrayList<>();

	public List<String> getRecentWallets() {
		return recentWallets;
	}

	public void setRecentWallets(List<String> recentWallets) {
		this.recentWallets = recentWallets;
	}

	public String getLastWalletFile() {
		return lastWalletFile;
	}

	public void setLastWalletFile(String lastWalletFile) {
		this.lastWalletFile = lastWalletFile;
	}

}
