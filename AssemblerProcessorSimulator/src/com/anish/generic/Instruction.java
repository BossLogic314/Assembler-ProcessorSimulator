package com.anish.generic;

public class Instruction {

	public enum OperationType {add, addi, sub, subi, mul, muli, div, divi, and, andi, or, ori, xor, xori, slt, slti, sll, slli,
		srl, srli, sra, srai, load, store, jmp, beq, bne, blt, bgt, end, push, ret;}
	
	public enum InstructionType {R3, R2I, RI;}
	
	private OperationType operationType;
	private InstructionType instructionType;
	private Operand source1;
	private Operand source2;
	private Operand dest;
	private int programCounter;
	
	// Constructor
	public Instruction() {
		operationType = null;
		instructionType = null;
		source1 = null;
		source2 = null;
		dest = null;
		programCounter = -1;
	}
	
	// Returns the instruction type of the instruction
	public static InstructionType findInstructionType(OperationType operationType) {
		
		switch(operationType) {
		case add:
		case sub:
		case mul:
		case div:
		case and:
		case or:
		case xor:
		case slt:
		case sll:
		case srl:
		case sra:
			return InstructionType.R3;
		
		case addi:
		case subi:
		case muli:
		case divi:
		case andi:
		case ori:
		case xori:
		case slti:
		case slli:
		case srli:
		case srai:
		case load:
		case store:
		case beq:
		case bne:
		case blt:
		case bgt:
			return InstructionType.R2I;
		
		case jmp:
		case end:
		case push:
		case ret:
			return InstructionType.RI;
		
		default:
			return null;
		}
	}

	// Getters and setters
	public OperationType getOperationType() {
		return operationType;
	}
	
	public void setOperationType(OperationType operationType) {
		this.operationType = operationType;
	}
	
	public InstructionType getInstructionType() {
		return instructionType;
	}
	
	public void setInstructionType(InstructionType instructionType) {
		this.instructionType = instructionType;
	}
	
	public Operand getSource1() {
		return source1;
	}

	public void setSource1(Operand source1) {
		this.source1 = source1;
	}

	public Operand getSource2() {
		return source2;
	}

	public void setSource2(Operand source2) {
		this.source2 = source2;
	}

	public Operand getDest() {
		return dest;
	}

	public void setDest(Operand dest) {
		this.dest = dest;
	}

	public int getProgramCounter() {
		return programCounter;
	}
	
	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}
}
