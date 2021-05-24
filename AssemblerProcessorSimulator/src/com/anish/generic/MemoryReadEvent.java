package com.anish.generic;

import com.anish.memory.Cache;

public class MemoryReadEvent extends Event {
	
	private int address;
	
	// While reading an address location, if it has to be updated in a cache
	private Cache cache;
	
	// Constructor
	public MemoryReadEvent(int fireTime, Handler requestingHandler, Handler processingHandler, int address) {
		super(fireTime, EventType.MemoryRead, requestingHandler, processingHandler);
		
		this.address = address;
		this.cache = null;
	}

	// Returns the address
	public int getAddress() {
		return address;
	}

	// Getter and setter of cache
	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}
}
