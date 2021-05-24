package com.anish.latches;

import com.anish.generic.Instruction;

public class MA_RW_Latch {

	private boolean isEnabled;
	private boolean isBusy;
	private Instruction instruction;
	
	// Constructor
	public MA_RW_Latch() {
		isEnabled = false;
		isBusy = false;
		instruction = null;
	}

	// Generate getters and setters
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

	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}
}
