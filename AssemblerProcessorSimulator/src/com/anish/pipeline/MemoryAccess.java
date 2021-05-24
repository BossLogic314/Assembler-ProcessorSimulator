package com.anish.pipeline;

import com.anish.configuration.Configuration;
import com.anish.generic.Event;
import com.anish.generic.Event.EventType;
import com.anish.generic.Handler;
import com.anish.generic.Instruction;
import com.anish.generic.Instruction.OperationType;
import com.anish.generic.MemoryReadEvent;
import com.anish.generic.MemoryResponseEvent;
import com.anish.generic.MemoryWriteEvent;
import com.anish.generic.Simulator;
import com.anish.latches.EX_MA_Latch;
import com.anish.latches.MA_RW_Latch;
import com.anish.memory.RegisterFile;
import com.anish.processor.Clock;
import com.anish.processor.Processor;

public class MemoryAccess implements Handler {

	private Processor processor;
	private EX_MA_Latch ex_ma_Latch;
	private MA_RW_Latch ma_rw_Latch;
	
	// Constructor
	public MemoryAccess(Processor processor, EX_MA_Latch ex_ma_Latch, MA_RW_Latch ma_rw_Latch) {
		this.processor = processor;
		this.ex_ma_Latch = ex_ma_Latch;
		this.ma_rw_Latch = ma_rw_Latch;
	}
	
	// Performs the memory access portion
	public void performMA() {
		
		// If the latch is not enabled, or is busy, keep waiting
		if (!ex_ma_Latch.isEnabled() || ex_ma_Latch.isBusy())
			return;
		
		// Extracting the instruction
		Instruction instruction = ex_ma_Latch.getInstruction();
		
		// Storing the type of the operation
		OperationType operationType = instruction.getOperationType();
		
		RegisterFile registerFile = processor.getRegisterFile();
		
		switch(operationType) {
		case load:
			// Creating and adding an event to the event queue
			MemoryReadEvent memoryReadEvent = new MemoryReadEvent(Clock.getTime() + Configuration.getL1dCacheLatency(), this,
					processor.getL1dCache(), registerFile.getResultExecute());
			Simulator.getEventQueue().addEvent(memoryReadEvent);
			break;
		case store:
			// Value in source1
			int source1Val = registerFile.getRegisterValue(instruction.getSource1().getValue());
			
			// To write into the cache
			MemoryWriteEvent memoryWriteEventCache = new MemoryWriteEvent(Clock.getTime() + Configuration.getL1dCacheLatency(), this,
					processor.getL1dCache(), registerFile.getResultExecute(), source1Val);
			
			// To write into the main memory
			MemoryWriteEvent memoryWriteEventMainMemory = new MemoryWriteEvent(Clock.getTime() + Configuration.getMainmemorylatency(), this,
					registerFile.getMainMemory(), registerFile.getResultExecute(), source1Val);
			
			// Adding the created events into the event queue
			Simulator.getEventQueue().addEvent(memoryWriteEventCache);
			Simulator.getEventQueue().addEvent(memoryWriteEventMainMemory);
			break;
		default:
			// Creating a dummy event for itself
			Event event = new Event(Clock.getTime(), EventType.PerformExecution, this, this);
			Simulator.getEventQueue().addEvent(event);
		}
		
		ex_ma_Latch.setBusy(true);
	}

	@Override
	// Deals with events when they're fired
	public void handleEvent(Event event) {
		
		// If the next latch is busy, keep waiting
		if (ma_rw_Latch.isBusy()) {
			event.setFireTime(Clock.getTime() + 1);
			Simulator.getEventQueue().addEvent(event);
			
			return;
		}
		
		// Reference to the register file
		RegisterFile registerFile = processor.getRegisterFile();
		
		// For a memory response event
		if (event.getEventType() == EventType.MemoryResponse) {
			MemoryResponseEvent memoryResponseEvent = (MemoryResponseEvent)event;
			
			// Data in the location
			int result = memoryResponseEvent.getResult();
			
			registerFile.setResultMemoryAccess(result);
		}
		// For all other events
		else
			registerFile.setResultMemoryAccess(registerFile.getResultExecute());
		
		// Forwarding the outputs of 'Execute' unit
		registerFile.setIsExtraMemoryAccess(registerFile.getIsExtraExecute());
		registerFile.setExtraValueMemoryAccess(registerFile.getExtraValueExecute());
		
		// All the activity is complete
		ma_rw_Latch.setInstruction(ex_ma_Latch.getInstruction());
		
		ex_ma_Latch.setInstruction(null);
		ex_ma_Latch.setEnabled(false);
		ex_ma_Latch.setBusy(false);
		
		ma_rw_Latch.setEnabled(true);
	}
}
