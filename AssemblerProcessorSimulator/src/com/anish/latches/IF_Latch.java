package com.anish.latches;

public class IF_Latch {

	private boolean isEnabled;
	private boolean isBusy;
	
	// Constructor
	public IF_Latch() {
		isEnabled = true;
		isBusy = false;
	}

	// Getters and setters
	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}
}
