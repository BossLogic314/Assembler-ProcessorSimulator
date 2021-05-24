package com.anish.pipeline;

import com.anish.generic.Event;
import com.anish.generic.Handler;
import com.anish.generic.Instruction;
import com.anish.generic.Instruction.OperationType;
import com.anish.generic.Operand;
import com.anish.generic.Simulator;
import com.anish.latches.MA_RW_Latch;
import com.anish.memory.RegisterFile;
import com.anish.processor.Processor;

public class RegisterWrite implements Handler {

	private Processor processor;
	private MA_RW_Latch ma_rw_Latch;
	
	// Constructor
	public RegisterWrite(Processor processor, MA_RW_Latch ma_rw_Latch) {
		this.processor = processor;
		this.ma_rw_Latch = ma_rw_Latch;
	}
	
	// Performs the activity of writing into registers
	public void performRW() {
		
		// If the latch is not enabled, there is nothing to do
		if (!ma_rw_Latch.isEnabled())
			return;
		
		Instruction instruction = ma_rw_Latch.getInstruction();
		
		// Storing the operation type of the instruction
		OperationType operationType = instruction.getOperationType();
		
		// Reference to the register file
		RegisterFile registerFile = processor.getRegisterFile();
		
		// When the last instruction is encountered
		if (operationType == OperationType.end) {
			Simulator.setSimulationComplete(true);
			return;
		}
		
		// For operations in which register values need to be updated
		if (operationType.ordinal() >= OperationType.add.ordinal() && operationType.ordinal() <= OperationType.load.ordinal()) {
			
			// Storing the destination operand
			Operand dest = instruction.getDest();
			
			// The value to be written into the register
			int result = registerFile.getResultMemoryAccess();
			
			// Writing the value into the register
			registerFile.setRegisterValue(dest.getValue(), result);
		}
		
		// If extra bits are to be stored
		if (registerFile.getIsExtraMemoryAccess())
			registerFile.setRegisterValue(31, registerFile.getExtraValueMemoryAccess());
		
		ma_rw_Latch.setEnabled(false);
		ma_rw_Latch.setInstruction(null);
	}

	@Override
	// Deals with events when they're fired
	public void handleEvent(Event event) {
		
		// This unit does not deal with events
		// This is implemented just for uniformity
	}
}
