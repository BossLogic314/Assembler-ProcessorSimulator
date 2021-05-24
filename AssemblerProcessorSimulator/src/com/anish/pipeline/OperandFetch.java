package com.anish.pipeline;

import java.util.ArrayList;

import com.anish.generic.Event;
import com.anish.generic.Handler;
import com.anish.generic.Instruction;
import com.anish.generic.Instruction.InstructionType;
import com.anish.generic.Instruction.OperationType;
import com.anish.generic.Operand;
import com.anish.generic.Simulator;
import com.anish.generic.Event.EventType;
import com.anish.generic.Operand.OperandType;
import com.anish.latches.IF_OF_Latch;
import com.anish.latches.OF_EX_Latch;
import com.anish.memory.RegisterFile;
import com.anish.processor.Clock;
import com.anish.processor.Processor;

public class OperandFetch implements Handler {

	private Processor processor;
	private IF_OF_Latch if_of_Latch;
	private OF_EX_Latch of_ex_Latch;
	
	// For opcode
	private final int opCodeShiftBits = 27;
	private final int opCodeExtractValue = 31;
	
	// For source1
	private final int source1ShiftBits = 22;
	private final int source1ExtractValue = 31;
	
	// For source2
	private final int source2R3ShiftBits = 17;
	private final int source2R3ExtractValue = 31;
	
	private final int source2R2IShiftBits = 15;
	private final int source2R2IExtractValue = 131071;
	
	private final int source2RIShiftBits = 10;
	private final int source2RIExtractValue = 4194303;
	
	// For destination operand
	private final int destR3ShiftBits = 12;
	private final int destR2IShiftBits = 17;
	private final int destRIShiftBits = 22;
	
	private final int destExtractVal = 31;
	
	// Constructor
	public OperandFetch(Processor processor, IF_OF_Latch if_of_Latch, OF_EX_Latch of_ex_Latch) {
		this.processor = processor;
		this.if_of_Latch = if_of_Latch;
		this.of_ex_Latch = of_ex_Latch;
	}
	
	// Returns whether any data hazards have been found or not
	private boolean findDataHazards(Instruction currentInstruction) {
		
		// Stores whether a data hazard has been encountered or not
		boolean dataHazards = false;
		
		// To store instructions in different latches
		ArrayList<Instruction> listOfInstructions = new ArrayList<Instruction>();
		
		// Adding the instructions from different latches to the list of instructions
		listOfInstructions.add(processor.getOF_EX_Latch().getInstruction());
		listOfInstructions.add(processor.getEX_MA_Latch().getInstruction());
		listOfInstructions.add(processor.getMA_RW_Latch().getInstruction());
		
		// Operation type of the current instruction
		OperationType currentOperationType = currentInstruction.getOperationType();
		
		// Storing all the operands of the current instruction
		Operand currentSource1 = currentInstruction.getSource1();
		Operand currentSource2 = currentInstruction.getSource2();
		Operand currentDest = currentInstruction.getDest();
		
		// Reference to the register file
		RegisterFile registerFile = processor.getRegisterFile();
		
		// To find out which latch the instruction belongs to
		int counter = 0;
		
		// Iterating through each instruction
		for (Instruction prevInstruction : listOfInstructions) {
			
			// If the execution of that stage is complete
			if (prevInstruction == null)
				continue;
			
			// Operation type of the next instruction
			OperationType prevOperationType = prevInstruction.getOperationType();
			
			// Storing all the operands of the next instruction
			Operand prevSource2 = prevInstruction.getSource2();
			Operand prevDest = prevInstruction.getDest();
			
			// If the previous instruction is between 'add' and 'srai'
			if (prevOperationType.ordinal() >= OperationType.add.ordinal() && prevOperationType.ordinal() <= OperationType.srai.ordinal()) {
				
				// If the current instruction also is between 'add' and 'srai'
				if (currentOperationType.ordinal() >= OperationType.add.ordinal() &&
						currentOperationType.ordinal() <= OperationType.srai.ordinal()) {
					
					if (currentSource1.getValue() == prevDest.getValue() ||
							(currentSource2.getOperandType() == OperandType.Register && currentSource2.getValue() == prevDest.getValue())) {
						dataHazards = true;
						break;
					}
					
					// If x31 is used
					else if (currentSource1.getValue() == 31 ||
							(currentSource2.getOperandType() == OperandType.Register && currentSource2.getValue() == 31)) {
						dataHazards = true;
						break;
					}
				}
				
				// If the current instruction is 'load'
				else if (currentOperationType == OperationType.load) {
					if (currentSource1.getValue() == prevDest.getValue()) {
						dataHazards = true;
						break;
					}
					
					// If x31 is used
					if (currentSource1.getValue() == 31) {
						dataHazards = true;
						break;
					}
				}
				
				// If the current instruction is 'store'
				else if (currentOperationType == OperationType.store) {
					if (currentSource1.getValue() == prevDest.getValue() || currentDest.getValue() == prevDest.getValue()) {
						dataHazards = true;
						break;
					}
					
					// If x31 is used
					if (currentSource1.getValue() == 31 || currentDest.getValue() == 31) {
						dataHazards = true;
						break;
					}
				}
				
				// If the current instruction is between 'beq' and 'bgt'
				else if (currentOperationType.ordinal() >= OperationType.beq.ordinal() &&
						currentOperationType.ordinal() <= OperationType.bgt.ordinal()) {
					
					if (currentSource1.getValue() == prevDest.getValue() || currentDest.getValue() == prevDest.getValue()) {
						dataHazards = true;
						break;
					}
					
					// If x31 is used
					if (currentSource1.getValue() == 31 || currentDest.getValue() == 31) {
						dataHazards = true;
						break;
					}
				}
			}
			
			// If the previous instruction is 'load'
			else if (prevOperationType == OperationType.load) {
				
				// If the current instruction is between 'add' and 'srai'
				if (currentOperationType.ordinal() >= OperationType.add.ordinal() &&
						currentOperationType.ordinal() <= OperationType.srai.ordinal()) {
					
					if (currentSource1.getValue() == prevDest.getValue() ||
							(currentSource2.getOperandType() == OperandType.Register && currentSource2.getValue() == prevDest.getValue())) {
						dataHazards = true;
						break;
					}
				}
				
				// If the current instruction is 'load'
				else if (currentOperationType == OperationType.load) {
					if (currentSource1.getValue() == prevDest.getValue()) {
						dataHazards = true;
						break;
					}
				}

				// If the current instruction is 'store'
				else if (currentOperationType == OperationType.store) {
					if (currentSource1.getValue() == prevDest.getValue() || currentDest.getValue() == prevDest.getValue()) {
						dataHazards = true;
						break;
					}
				}
				
				// If the current instruction is between 'beq' and 'bgt'
				else if (currentOperationType.ordinal() >= OperationType.beq.ordinal() &&
						currentOperationType.ordinal() <= OperationType.bgt.ordinal()) {
					
					if (currentSource1.getValue() == prevDest.getValue() || currentDest.getValue() == prevDest.getValue()) {
						dataHazards = true;
						break;
					}
				}
			}
			
			// If the previous instruction is 'store'
			else if (prevOperationType == OperationType.store) {
				
				// If the current instruction is 'load'
				if (currentOperationType == OperationType.load) {
					
					// This data hazard does not occur when memory is already written
					if ((counter != 2) && registerFile.getRegisterValue(currentSource1.getValue()) + currentSource2.getValue() ==
							registerFile.getRegisterValue(prevDest.getValue()) + prevSource2.getValue()) {
						dataHazards = true;
						break;
					}
				}
			}
			
			++counter;
		}
		
		return dataHazards;
	}
	
	// Returns the source operand 1
	private Operand getSource1(int instructionCode, InstructionType instructionType) {
		
		// There is no source1 operand for RI instructions
		if (instructionType == InstructionType.RI)
			return null;
		
		// Extracting the value
		int source1Value = (instructionCode >>> source1ShiftBits) & source1ExtractValue;
		
		// Creating a new source 1 operand
		Operand source1 = new Operand();
		source1.setOperandType(OperandType.Register);
		source1.setValue(source1Value);
		
		return source1;
	}
	
	// Returns the source operand 2
	private Operand getSource2(int instructionCode, InstructionType instructionType) {
		
		Operand source2 = new Operand();
		
		// If the instruction is of R3 type
		if (instructionType == InstructionType.R3) {
			
			source2.setOperandType(OperandType.Register);
			
			// Extracting and setting the value to the operand
			int source2Value = (instructionCode >>> source2R3ShiftBits) & source2R3ExtractValue;
			source2.setValue(source2Value);
			
			return source2;
		}
		
		// If not of R3 type
		source2.setOperandType(OperandType.Immediate);
		
		// To store the value
		int source2Value = 0;
		
		// If the instruction is of R2I type
		if (instructionType == InstructionType.R2I) {
			
			// Extracting the value
			source2Value = instructionCode & source2R2IExtractValue;
			
			// Making sure the negative values are taken as negative
			source2Value = (source2Value << source2R2IShiftBits) >> source2R2IShiftBits;
		}
		
		// If the instruction is of RI type
		else if (instructionType == InstructionType.RI) {
			
			// Extracting the value
			source2Value = instructionCode & source2RIExtractValue;
			
			// Making sure the negative values are taken as negative
			source2Value = (source2Value << source2RIShiftBits) >> source2RIShiftBits;
		}
		
		// Setting the value into the operand
		source2.setValue(source2Value);
		
		return source2;
	}
	
	// Returns the destination operand
	private Operand getDest(int instructionCode, InstructionType instructionType) {
		
		// Creating and setting the operand type of the destination operand
		Operand dest = new Operand();
		dest.setOperandType(OperandType.Register);
		
		// To store the value
		int destVal = 0;
		
		if (instructionType == InstructionType.R3)
			destVal = (instructionCode >>> destR3ShiftBits) & destExtractVal;
		else if (instructionType == InstructionType.R2I)
			destVal = (instructionCode >>> destR2IShiftBits) & destExtractVal;
		else
			destVal = (instructionCode >>> destRIShiftBits) & destExtractVal;
		
		// Setting the value into the operand
		dest.setValue(destVal);
		
		return dest;
	}
	
	// This function returns an instance of 'Instruction' from the instruction code
	private Instruction prepareInstruction() {
		
		// Preparing the instruction
		int instructionCode = if_of_Latch.getInstructionCode();
		
		// Extracting the value of the op code
		int opCode = (instructionCode >>> opCodeShiftBits) & opCodeExtractValue;
		
		// Getting the instruction type
		OperationType operationType = OperationType.values()[opCode];
		
		Instruction instruction = new Instruction();
		instruction.setOperationType(operationType);
		
		// Finding the instruction type and setting it to the instruction
		InstructionType instructionType = Instruction.findInstructionType(operationType);
		instruction.setInstructionType(instructionType);
		
		// Getting the operands of the instructions
		Operand source1 = getSource1(instructionCode, instructionType);
		Operand source2 = getSource2(instructionCode, instructionType);
		Operand dest = getDest(instructionCode, instructionType);
		
		// Setting the operands into the instruction
		instruction.setSource1(source1);
		instruction.setSource2(source2);
		instruction.setDest(dest);
		
		// Assigning it the appropriate program counter
		instruction.setProgramCounter(processor.getRegisterFile().getProgramCounter() - 1);
		
		// Returning the instruction
		return instruction;
	}
	
	// Fetches the operands
	public void performOF() {
		
		// If the latch is not enabled, or is busy, keep waiting
		if (!if_of_Latch.isEnabled() || if_of_Latch.isBusy())
			return;
		
		// Creating an event and adding it to the event queue
		Event event = new Event(Clock.getTime(), EventType.PerformExecution, this, this);
		Simulator.getEventQueue().addEvent(event);
		
		if_of_Latch.setBusy(true);
	}

	@Override
	// Deals with events when they're fired
	public void handleEvent(Event event) {
		
		// If the next latch is busy
		if (of_ex_Latch.isBusy()) {
			event.setFireTime(Clock.getTime() + 1);
			Simulator.getEventQueue().addEvent(event);
			
			return;
		}
		
		// Calling the function to prepare the instruction
		Instruction instruction = prepareInstruction();
		
		// If data hazards were detected
		if (findDataHazards(instruction)) {
			event.setFireTime(Clock.getTime() + 1);
			Simulator.getEventQueue().addEvent(event);
			
			return;
		}
		
		// If the control flow reaches here, no data hazards were detected
		
		// Setting the counter of the instruction
		instruction.setProgramCounter(processor.getRegisterFile().getProgramCounter() - 1);
		
		of_ex_Latch.setInstruction(instruction);
		
		if_of_Latch.setBusy(false);
		if_of_Latch.setEnabled(false);
		
		of_ex_Latch.setEnabled(true);
	}
}
