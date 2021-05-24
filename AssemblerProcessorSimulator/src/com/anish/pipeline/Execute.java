package com.anish.pipeline;

import java.util.ArrayList;

import com.anish.configuration.Configuration;
import com.anish.generic.Instruction;
import com.anish.generic.Errors;
import com.anish.generic.Event;
import com.anish.generic.Event.EventType;
import com.anish.generic.EventQueue;
import com.anish.generic.Handler;
import com.anish.generic.Instruction.OperationType;
import com.anish.generic.Operand;
import com.anish.generic.Simulator;
import com.anish.generic.Operand.OperandType;
import com.anish.latches.EX_MA_Latch;
import com.anish.latches.OF_EX_Latch;
import com.anish.memory.RegisterFile;
import com.anish.processor.Clock;
import com.anish.processor.Processor;

public class Execute implements Handler {

	private Processor processor;
	private OF_EX_Latch of_ex_Latch;
	private EX_MA_Latch ex_ma_Latch;
	
	// Constructor
	public Execute(Processor processor, OF_EX_Latch of_ex_Latch, EX_MA_Latch ex_ma_Latch) {
		this.processor = processor;
		this.of_ex_Latch = of_ex_Latch;
		this.ex_ma_Latch = ex_ma_Latch;
	}
	
	// This function performs the execution of arithmetic instructions
	private void executeArithmeticInstructions(Instruction instruction) {
		
		// Extracting the operands
		Operand source1 = instruction.getSource1();
		Operand source2 = instruction.getSource2();
		
		// To store the operand values
		int source1Val = 0, source2Val = 0;
		
		// Storing a reference to the register file
		RegisterFile registerFile = processor.getRegisterFile();
		
		// Finding the value of source1
		if (source1.getOperandType() == OperandType.Register)
			source1Val = registerFile.getRegisterValue(source1.getValue());
		else
			source1Val = source1.getValue();
		
		// Finding the value of source2
		if (source2.getOperandType() == OperandType.Register)
			source2Val = registerFile.getRegisterValue(source2.getValue());
		else
			source2Val = source2.getValue();
		
		// Storing the type of the operation
		OperationType operationType = instruction.getOperationType();
		
		// To store the result of the operation
		long result = 0;
		
		// To find out whether extra bits are obtained by calculation
		boolean isExtra = false;
		
		// To store the extra bits
		int extraValue = 0;
		
		switch(operationType) {
		case add:
		case addi:
			result = (long)source1Val + source2Val;
			extraValue = (int)(result >> 32);
			if (extraValue != 0)
				isExtra = true;
			break;
		case sub:
		case subi:
			result = (long)source1Val - source2Val;
			extraValue = (int)(result >> 32);
			if (extraValue != 0)
				isExtra = true;
			break;
		case mul:
		case muli:
			result = (long)source1Val * source2Val;
			extraValue = (int)(result >> 32);
			if (extraValue != 0)
				isExtra = true;
			break;
		case div:
		case divi:
			result = (long)source1Val / source2Val;
			extraValue = (int)source1Val % source2Val;
			if (extraValue != 0)
				isExtra = true;
			break;
		case and:
		case andi:
			result = (long)source1Val & source2Val;
			extraValue = (int)(result >> 32);
			if (extraValue != 0)
				isExtra = true;
			break;
		case or:
		case ori:
			result = (long)source1Val | source2Val;
			extraValue = (int)(result >> 32);
			if (extraValue != 0)
				isExtra = true;
			break;
		case xor:
		case xori:
			result = (long)source1Val ^ source2Val;
			extraValue = (int)(result >> 32);
			if (extraValue != 0)
				isExtra = true;
			break;
		case slt:
		case slti:
			result = source1Val < source2Val ? 1 : 0;
			break;
		case sll:
		case slli:
			// If the value has to be left shifted by more than 32 bits or less than 0 bits
			if (source2Val < 0 || source2Val > 31)
				Errors.printShiftingError();
			
			extraValue = source1Val >>> (32 - source2Val);
			result = source1Val << source2Val;
			isExtra = true;
			break;
		case srl:
		case srli:
			// If the value has to be right shifted by more than 32 bits or less than 0 bits
			if (source2Val < 0 || source2Val > 31)
				Errors.printShiftingError();
			
			extraValue = (source1Val << (32 - source2Val)) >>> (32 - source2Val);
			result = source1Val >>> source2Val;
			isExtra = true;
			break;
		case sra:
		case srai:
			// If the value has to be right shifted by more than 32 bits or less than 0 bits
			if (source2Val < 0 || source2Val > 31)
				Errors.printShiftingError();
			
			extraValue = (source1Val << (32 - source2Val)) >>> (32 - source2Val);
			result = source1Val >> source2Val;
			isExtra = true;
			break;
		default:
			break;
		}
		
		registerFile.setIsExtraExecute(isExtra);
		
		// If extra bits are to be stored
		if (isExtra)
			registerFile.setExtraValueExecute(extraValue);
		
		// Adding the result of the computation to the register file
		registerFile.setResultExecute((int)result);
	}
	
	// This function performs the execution of control flow instructions
	private void executeControlFlowInstructions(Instruction instruction) {
		
		// Extracting the operands
		Operand source1 = instruction.getSource1();
		Operand source2 = instruction.getSource2();
		Operand dest = instruction.getDest();
		
		// Reference to the register file
		RegisterFile registerFile = processor.getRegisterFile();
		
		// To store the operand values
		int source1Val = 0, source2Val = 0, destVal = 0;
		
		if (source1 != null)
			source1Val = registerFile.getRegisterValue(source1.getValue());
		
		if (source2 != null)
			source2Val = source2.getValue();
		
		if (dest != null)
			destVal = registerFile.getRegisterValue(dest.getValue());
		
		OperationType operationType = instruction.getOperationType();
		
		// Stores whether the program counter has to jump to some other value
		boolean toJump = false;
		
		// Stores the number of address units to jump
		int pcJump = 0;
		
		// Performing the execution
		switch(operationType) {
		case jmp:
			toJump = true;
			pcJump = source2Val + destVal;
			break;
		case beq:
			if (source1Val == destVal) {
				toJump = true;
				pcJump = source2Val;
			}
			break;
		case bne:
			if (source1Val != destVal) {
				toJump = true;
				pcJump = source2Val;
			}
			break;
		case blt:
			if (source1Val < destVal) {
				toJump = true;
				pcJump = source2Val;
			}
			break;
		case bgt:
			if (source1Val > destVal) {
				toJump = true;
				pcJump = source2Val;
			}
			break;
		default:
			break;
		}
		
		// If the control flow must not jump, there is nothing to do
		if (!toJump)
			return;
		
		// If the next instruction to be fetched is the following one, there is nothing to do
		if (pcJump == 1)
			return;
		
		// Reference to the event queue
		EventQueue eventQueue = Simulator.getEventQueue();
		
		// To store the list of events in the event queue
		ArrayList<Event> listOfEvents = new ArrayList<Event>();
		
		// Popping all the events from the event queue
		while (!eventQueue.isEventQueueEmpty()) {
			listOfEvents.add(eventQueue.popEvent());
		}
		
		// Iterating through all the events in the list of events
		for (Event event : listOfEvents) {
			
			// Removing all the events with respect to the IF and OF stage
			if (event.getRequestingHandler() != processor.getIF_Unit() && event.getRequestingHandler() != processor.getOF_Unit())
				eventQueue.addEvent(event);
		}
		
		// Re-setting the program counter
		registerFile.setProgramCounter(instruction.getProgramCounter() + pcJump);
		
		// Fetching the instructions afresh
		processor.getIF_Latch().setBusy(false);
		processor.getIF_OF_Latch().setBusy(false);
		processor.getIF_OF_Latch().setEnabled(false);
	}
	
	// This function performs the execution of memory instructions
	private void executeMemoryInstructions(Instruction instruction) {
		
		OperationType operationType = instruction.getOperationType();
		
		// Storing the operands
		Operand source1 = instruction.getSource1();
		Operand source2 = instruction.getSource2();
		Operand dest = instruction.getDest();
		
		// Reference to the register file
		RegisterFile registerFile = processor.getRegisterFile();
		
		int source1Val = registerFile.getRegisterValue(source1.getValue());
		int source2Val = source2.getValue();
		int destVal = registerFile.getRegisterValue(dest.getValue());
		
		// To store the result
		int result = 0;
		
		// Performing the execution
		switch(operationType) {
		case load:
			result = source1Val + source2Val;
			break;
		case store:
			result = destVal + source2Val;
			break;
		default:
			break;
		}
		
		// Setting the result value
		registerFile.setResultExecute(result);
	}
	
	// Performs the execution
	public void performEX() {
		
		// If the latch is not enabled, or is busy, keep waiting
		if (!of_ex_Latch.isEnabled() || of_ex_Latch.isBusy())
			return;
		
		// Extracting the instruction from the latch
		Instruction instruction = of_ex_Latch.getInstruction();
		
		// To store the latency of the instruction
		int latency = 0;
		
		// Storing the operation type of the instruction
		OperationType operationType = instruction.getOperationType();
		
		// Determining the latency
		if (operationType == OperationType.add || operationType == OperationType.addi)
			latency = Configuration.getAdditionlatency();
		else if (operationType == OperationType.mul || operationType == OperationType.muli)
			latency = Configuration.getMultiplicationlatency();
		else if (operationType == OperationType.div || operationType == OperationType.divi)
			latency = Configuration.getDivisionlatency();
		else if (operationType.ordinal() >= OperationType.add.ordinal() && operationType.ordinal() <= OperationType.srai.ordinal())
			latency = Configuration.getALULatency();
		
		// Creating a new event and adding it to the event queue
		Event performExecutionEvent = new Event(Clock.getTime() + latency, EventType.PerformExecution, this, this);
		Simulator.getEventQueue().addEvent(performExecutionEvent);
		
		of_ex_Latch.setBusy(true);
	}

	@Override
	// Deals with events when they're fired
	public void handleEvent(Event event) {
		
		// If the next latch is busy, keep waiting
		if (ex_ma_Latch.isBusy()) {
			event.setFireTime(Clock.getTime() + 1);
			Simulator.getEventQueue().addEvent(event);
			
			return;
		}
		
		// Extracting the instruction from the latch
		Instruction instruction = of_ex_Latch.getInstruction();
		
		// Storing the operation type of the instruction
		OperationType operationType = instruction.getOperationType();
		
		// For arithmetic instructions
		if (operationType.ordinal() >= OperationType.add.ordinal() && operationType.ordinal() <= OperationType.srai.ordinal())
			executeArithmeticInstructions(instruction);
		// For control flow instructions
		else if (operationType.ordinal() >= OperationType.jmp.ordinal() && operationType.ordinal() <= OperationType.bgt.ordinal())
			executeControlFlowInstructions(instruction);
		// For memory instructions
		else if (operationType.ordinal() >= OperationType.load.ordinal() && operationType.ordinal() <= OperationType.store.ordinal())
			executeMemoryInstructions(instruction);
		
		// After the execution is complete
		ex_ma_Latch.setInstruction(instruction);
		
		of_ex_Latch.setInstruction(null);
		of_ex_Latch.setEnabled(false);
		of_ex_Latch.setBusy(false);
		
		ex_ma_Latch.setEnabled(true);
	}
}
