package com.anish.generic;

public class MemoryResponseEvent extends Event {
	
	private int result;
	
	// Constructor
	public MemoryResponseEvent(int fireTime, Handler requestingHandler, Handler processingHandler, int result) {
		super(fireTime, EventType.MemoryResponse, requestingHandler, processingHandler);
		
		this.result = result;
	}

	// Returns the value in the memory address
	public int getResult() {
		return result;
	}
}
