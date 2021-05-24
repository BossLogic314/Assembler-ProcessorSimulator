package com.anish.pipeline;

import com.anish.configuration.Configuration;
import com.anish.generic.Event;
import com.anish.generic.Event.EventType;
import com.anish.generic.Handler;
import com.anish.generic.MemoryReadEvent;
import com.anish.generic.MemoryResponseEvent;
import com.anish.generic.Simulator;
import com.anish.latches.IF_Latch;
import com.anish.latches.IF_OF_Latch;
import com.anish.memory.RegisterFile;
import com.anish.processor.Clock;
import com.anish.processor.Processor;

public class InstructionFetch implements Handler {

	private Processor processor;
	private IF_Latch if_Latch;
	private IF_OF_Latch if_of_Latch;
	
	// Constructor
	public InstructionFetch(Processor processor, IF_Latch if_Latch, IF_OF_Latch if_of_Latch) {
		this.processor = processor;
		this.if_Latch = if_Latch;
		this.if_of_Latch = if_of_Latch;
	}
	
	// Fetches the instruction
	public void performIF() {
		
		// If the latch is not enabled, there is nothing to do
		// If the latch is busy, wait for another clock cycle
		if (!if_Latch.isEnabled() || if_Latch.isBusy())
			return;
		
		// Storing a reference to the register file
		RegisterFile registerFile = processor.getRegisterFile();
		
		// Getting the program counter
		int programCounter = registerFile.getProgramCounter();
		
		// If all the instructions are already read, there is nothing more to do
		if (programCounter > registerFile.getLastInstructionCounter())
			return;
		
		// Sending the read request to the cache
		MemoryReadEvent memoryReadEvent = new MemoryReadEvent(Clock.getTime() + Configuration.getL1iCacheLatency(), this, processor.getL1iCache(),
				programCounter);
		
		// Adding the event into the queue
		Simulator.getEventQueue().addEvent(memoryReadEvent);
		
		if_Latch.setBusy(true);
	}

	@Override
	// Deals with events when they're fired
	public void handleEvent(Event event) {
		
		// If the next latch is busy, keep waiting
		if (if_of_Latch.isBusy()) {
			event.setFireTime(Clock.getTime() + 1);
			Simulator.getEventQueue().addEvent(event);
			
			return;
		}
		
		// For a memory response event
		if (event.getEventType() == EventType.MemoryResponse) {
			
			MemoryResponseEvent memoryResponseEvent = (MemoryResponseEvent)event;
			
			// Getting the instruction
			int instructionCode = memoryResponseEvent.getResult();
			
			if_of_Latch.setInstructionCode(instructionCode);
			if_of_Latch.setEnabled(true);
			if_Latch.setBusy(false);
			
			// Reference to the register file
			RegisterFile registerFile = processor.getRegisterFile();
			
			// Updating the program counter
			registerFile.setProgramCounter(registerFile.getProgramCounter() + 1);
		}
	}
}
