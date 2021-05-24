package com.anish.memory;

import com.anish.configuration.Configuration;
import com.anish.generic.Event;
import com.anish.generic.Event.EventType;
import com.anish.generic.Handler;
import com.anish.generic.MemoryReadEvent;
import com.anish.generic.MemoryResponseEvent;
import com.anish.generic.MemoryWriteEvent;
import com.anish.generic.Simulator;
import com.anish.processor.Clock;

public class Cache implements Handler {

	private final int numOfCacheLines = 1;
	private CacheLine[] cacheLines;
	
	// If any tag is not present in the cache, it is added in this index
	private int overWriteIndex;
	
	// Constructor
	public Cache() {
		cacheLines = new CacheLine[numOfCacheLines];
		
		// Allocating memory for all the cache lines
		for (int i = 0; i < numOfCacheLines; ++i)
			cacheLines[i] = new CacheLine();
		
		// Initially, there wouldn't be any tags stored in the cache
		overWriteIndex = 0;
	}
	
	// Reads the memory from the provided address
	private void cacheRead(MemoryReadEvent memoryReadEvent) {
		
		// Storing the address to read from
		int tag = memoryReadEvent.getAddress();
		
		// Searching for the tag in all the cache lines
		for (CacheLine cacheLine : cacheLines) {
			
			// If the tag is already present in the cache
			if (cacheLine.getTag() == tag) {
				
				// Storing the value stored in the cache for the tag
				int value = cacheLine.getValue();
				
				// Creating an event for the unit
				MemoryResponseEvent memoryResponseEvent = new MemoryResponseEvent(Clock.getTime(), this, memoryReadEvent.getRequestingHandler(),
						value);
				
				// Adding the event into the queue
				Simulator.getEventQueue().addEvent(memoryResponseEvent);
				
				return;
			}
		}
		
		// If control flow reaches here, the tag is not present in the cache
		
		// Creating a new memory read event which is processed by the main memory
		MemoryReadEvent mainMemoryReadEvent = new MemoryReadEvent(Clock.getTime() + Configuration.getMainmemorylatency(),
				memoryReadEvent.getRequestingHandler(), Simulator.getProcessor().getRegisterFile().getMainMemory(), tag);
		
		// Setting the cache attribute before sending this event to the main memory
		mainMemoryReadEvent.setCache(this);
		
		// Adding this event into the queue
		Simulator.getEventQueue().addEvent(mainMemoryReadEvent);
	}
	
	// Writes memory into the cache
	private void cacheWrite(MemoryWriteEvent memoryWriteEvent) {
		
		// Storing the address and the value to be written into the cache
		int tag = memoryWriteEvent.getAddress();
		int value = memoryWriteEvent.getValue();
		
		// Searching for the tag in the cache
		for (CacheLine cacheLine : cacheLines) {
			
			// If the tag is already present in the cache
			if (cacheLine.getTag() == tag) {
				
				// Updating the value
				cacheLine.setValue(value);
				
				// Creating an execution complete event back to the unit
				Event executionCompleteEvent = new Event(Clock.getTime(), EventType.ExecutionComplete, this,
						memoryWriteEvent.getRequestingHandler());
				
				// Adding the event to the event queue
				Simulator.getEventQueue().addEvent(executionCompleteEvent);
				
				return;
			}
		}
		
		// If control flow reaches here, the tag is already not present in the cache
		
		// Calling the function to insert information into the cache
		handleResponse(tag, value);
		
		// Creating an execution complete event back to the unit
		Event executionCompleteEvent = new Event(Clock.getTime(), EventType.ExecutionComplete, this,
				memoryWriteEvent.getRequestingHandler());
		
		// Adding the event to the event queue
		Simulator.getEventQueue().addEvent(executionCompleteEvent);
	}
	
	// Inserts a tag and its value into the cache, invoked by the main memory
	public void handleResponse(int tag, int value) {
		
		// Inserting the information into the cache
		cacheLines[overWriteIndex].setTag(tag);
		cacheLines[overWriteIndex++].setValue(value);
		
		// Updating the overwrite index
		if (overWriteIndex >= numOfCacheLines)
			overWriteIndex = 0;
	}

	@Override
	// Deals with the events fired in the event queue
	public void handleEvent(Event event) {
		
		// For a memory read event
		if (event.getEventType() == EventType.MemoryRead) {
			
			MemoryReadEvent memoryReadEvent = (MemoryReadEvent)event;
			
			// Calling the function to read memory from the provided address
			cacheRead(memoryReadEvent);
		}
		
		// For a memory write event
		if (event.getEventType() == EventType.MemoryWrite) {
			
			MemoryWriteEvent memoryWriteEvent = (MemoryWriteEvent)event;
			
			// Calling the function to write the memory into the cache
			cacheWrite(memoryWriteEvent);
		}
	}
}
