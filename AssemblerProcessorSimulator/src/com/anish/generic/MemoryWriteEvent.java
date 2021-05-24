package com.anish.generic;

public class MemoryWriteEvent extends Event {

	private int address;
	private int value;
	
	// Constructor
	public MemoryWriteEvent(int fireTime, Handler requestingHandler, Handler processingHandler, int address, int value) {
		super(fireTime, EventType.MemoryWrite, requestingHandler, processingHandler);
		
		this.address = address;
		this.value = value;
	}

	// Getters
	public int getAddress() {
		return address;
	}

	public int getValue() {
		return value;
	}
}
