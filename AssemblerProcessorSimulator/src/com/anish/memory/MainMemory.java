package com.anish.memory;

import com.anish.generic.Event;
import com.anish.generic.Event.EventType;
import com.anish.processor.Clock;
import com.anish.generic.Handler;
import com.anish.generic.MemoryReadEvent;
import com.anish.generic.MemoryResponseEvent;
import com.anish.generic.MemoryWriteEvent;
import com.anish.generic.Simulator;

public class MainMemory implements Handler {

	private static int memorySize = (int)Math.pow(2, 16);
	private int mainMemory[];
	
	// Constructor
	public MainMemory() {
		mainMemory = new int[memorySize];
	}
	
	// Returns the value in an address
	public int getAddressValue(int address) {
		return mainMemory[address];
	}
	
	// Sets the value in the specified address
	public void setAddressValue(int address, int value) {
		mainMemory[address] = value;
	}
	
	// Returns the size of the main memory
	public int getMemorySize() {
		return memorySize;
	}

	@Override
	// To deal with the events when fired
	public void handleEvent(Event event) {
		
		// For a memory read event invoked by the cache
		if (event.getEventType() == EventType.MemoryRead) {
			
			MemoryReadEvent memoryReadEvent = (MemoryReadEvent)event;
			
			// Storing the address to read from
			int address = memoryReadEvent.getAddress();
			
			// Storing the value in the address
			int value = mainMemory[address];
			
			// Creating a memory request event back to the unit
			MemoryResponseEvent memoryResponseEvent = new MemoryResponseEvent(Clock.getTime(), this, memoryReadEvent.getRequestingHandler(),
					value);
			
			// Adding the event to the event queue
			Simulator.getEventQueue().addEvent(memoryResponseEvent);
			
			// Adding this data into the cache
			memoryReadEvent.getCache().handleResponse(address, value);
		}
		
		// For a memory write event
		else if (event.getEventType() == EventType.MemoryWrite) {
			
			MemoryWriteEvent memoryWriteEvent = (MemoryWriteEvent)event;
			
			// Storing the address and the value to be updated
			int address = memoryWriteEvent.getAddress();
			int value = memoryWriteEvent.getValue();
			
			mainMemory[address] = value;
		}
	}
}
