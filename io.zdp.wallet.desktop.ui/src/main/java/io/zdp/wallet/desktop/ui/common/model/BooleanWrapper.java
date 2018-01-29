package io.zdp.wallet.desktop.ui.common.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BooleanWrapper implements Serializable {

	private boolean value;

	public boolean isValue() {
		return value;
	}

	public BooleanWrapper() {
		super();
	}

	public BooleanWrapper(boolean value) {
		super();
		this.value = value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "BooleanWrapper [value=" + value + "]";
	}

}
