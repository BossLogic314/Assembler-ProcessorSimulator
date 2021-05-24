package com.anish.memory;

public class CacheLine {

	private int tag;
	private int value;
	
	// Constructor
	public CacheLine() {
		this.tag = -1;
		this.value = -1;
	}

	// Getters and setters
	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
