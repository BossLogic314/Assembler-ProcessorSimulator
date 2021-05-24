package com.anish.memory;

public class RegisterFile {

	private MainMemory mainMemory;
	
	private final int numberOfRegisters = (int)Math.pow(2, 5);
	private int [] registerFile;
	
	// Storing the program counter and the total number of instructions
	private int programCounter;
	private int lastInstructionCounter;
	
	// To store the results from 'Execute' stage
	private int resultExecute;
	private int extraValueExecute;
	private boolean isExtraExecute;
	
	// To store the results from the 'Memory Access' stage
	private int resultMemoryAccess;
	private int extraValueMemoryAccess;
	private boolean isExtraMemoryAccess;
	
	// Constructor
	public RegisterFile() {
		mainMemory = new MainMemory();
		registerFile = new int [numberOfRegisters];
		
		programCounter = -1;
		lastInstructionCounter = -1;
	}
	
	// Returns the instance of the MainMemory
	public MainMemory getMainMemory() {
		return mainMemory;
	}
	
	// Returns the value stored in the provided register
	public int getRegisterValue(int registerNumber) {
		
		return registerFile[registerNumber];
	}
	
	// Sets the provided number into the given register
	public void setRegisterValue(int registerNumber, int value) {
		
		registerFile[registerNumber] = value;
	}
	
	// Returns the total number of instructions
	public int getLastInstructionCounter() {
		return lastInstructionCounter;
	}
	
	// Sets the total number of instructions
	public void setLastInstructionCounter(int lastInstructionCounter) {
		this.lastInstructionCounter = lastInstructionCounter;
	}
	
	// Returns the number of registers
	public int getNumberOfRegisters() {
		return numberOfRegisters;
	}
	
	// Returns the program counter
	public int getProgramCounter() {
		return programCounter;
	}
	
	// Sets the program counter
	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}
	
	// Getters and setters of information holders of the 'Execute' unit
	public int getResultExecute() {
		return resultExecute;
	}
	
	public void setResultExecute(int resultExecute) {
		this.resultExecute = resultExecute;
	}
	
	public int getExtraValueExecute() {
		return extraValueExecute;
	}
	
	public void setExtraValueExecute(int extraValueExecute) {
		this.extraValueExecute = extraValueExecute;
	}
	
	public boolean getIsExtraExecute() {
		return isExtraExecute;
	}
	
	public void setIsExtraExecute(boolean isExtraExecute) {
		this.isExtraExecute = isExtraExecute;
	}
	
	// Getters and setters of information holders of the 'Memory Access' unit
	public int getResultMemoryAccess() {
		return resultMemoryAccess;
	}
	
	public void setResultMemoryAccess(int resultMemoryAccess) {
		this.resultMemoryAccess = resultMemoryAccess;
	}
	
	public int getExtraValueMemoryAccess() {
		return extraValueMemoryAccess;
	}
	
	public void setExtraValueMemoryAccess(int extraValueMemoryAccess) {
		this.extraValueMemoryAccess = extraValueMemoryAccess;
	}
	
	public boolean getIsExtraMemoryAccess() {
		return isExtraMemoryAccess;
	}
	
	public void setIsExtraMemoryAccess(boolean isExtraMemoryAccess) {
		this.isExtraMemoryAccess = isExtraMemoryAccess;
	}
}
