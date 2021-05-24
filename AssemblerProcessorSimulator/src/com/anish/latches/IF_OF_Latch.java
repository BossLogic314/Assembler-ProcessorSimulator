package com.anish.latches;

public class IF_OF_Latch {

	private boolean isEnabled;
	private boolean isBusy;
	private int instructionCode;
	
	// Constructor
	public IF_OF_Latch() {
		isEnabled = false;
		isBusy = false;
		instructionCode = 0;
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

	public int getInstructionCode() {
		return instructionCode;
	}

	public void setInstructionCode(int instructionCode) {
		this.instructionCode = instructionCode;
	}
}
