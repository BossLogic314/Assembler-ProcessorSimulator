package com.anish.generic;

public class Operand {

	public enum OperandType {Register, Immediate, Label, Function, Parameter;}
	
	private OperandType operandType;
	private int value;
	
	// Constructor
	public Operand() {
		operandType = null;
		value = 0;
	}
	
	// Getters and setters
	public OperandType getOperandType() {
		return operandType;
	}
	public void setOperandType(OperandType operandType) {
		this.operandType = operandType;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
