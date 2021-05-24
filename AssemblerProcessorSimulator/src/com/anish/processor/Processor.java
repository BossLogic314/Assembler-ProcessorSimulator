package com.anish.processor;

import com.anish.latches.EX_MA_Latch;
import com.anish.latches.IF_Latch;
import com.anish.latches.IF_OF_Latch;
import com.anish.latches.MA_RW_Latch;
import com.anish.latches.OF_EX_Latch;
import com.anish.memory.Cache;
import com.anish.memory.RegisterFile;
import com.anish.pipeline.Execute;
import com.anish.pipeline.InstructionFetch;
import com.anish.pipeline.MemoryAccess;
import com.anish.pipeline.OperandFetch;
import com.anish.pipeline.RegisterWrite;

public class Processor {

	// Register file
	private RegisterFile registerFile;
	
	// Latches
	private IF_Latch if_Latch;
	private IF_OF_Latch if_of_Latch;
	private OF_EX_Latch of_ex_Latch;
	private EX_MA_Latch ex_ma_Latch;
	private MA_RW_Latch ma_rw_Latch;
	
	// Units
	private InstructionFetch IF_Unit;
	private OperandFetch OF_Unit;
	private Execute EX_Unit;
	private MemoryAccess MA_Unit;
	private RegisterWrite RW_Unit;
	
	// Caches
	private Cache l1iCache;
	private Cache l1dCache;
	
	// Constructor
	public Processor() {
		registerFile = new RegisterFile();
		
		if_Latch = new IF_Latch();
		if_of_Latch = new IF_OF_Latch();
		of_ex_Latch = new OF_EX_Latch();
		ex_ma_Latch = new EX_MA_Latch();
		ma_rw_Latch = new MA_RW_Latch();
		
		IF_Unit = new InstructionFetch(this, if_Latch, if_of_Latch);
		OF_Unit = new OperandFetch(this, if_of_Latch, of_ex_Latch);
		EX_Unit = new Execute(this, of_ex_Latch, ex_ma_Latch);
		MA_Unit = new MemoryAccess(this, ex_ma_Latch, ma_rw_Latch);
		RW_Unit = new RegisterWrite(this, ma_rw_Latch);
		
		l1iCache = new Cache();
		l1dCache = new Cache();
	}
	
	// Returns the register file
	public RegisterFile getRegisterFile() {
		return registerFile;
	}

	// Getters of latches
	public IF_Latch getIF_Latch() {
		return if_Latch;
	}

	public IF_OF_Latch getIF_OF_Latch() {
		return if_of_Latch;
	}

	public OF_EX_Latch getOF_EX_Latch() {
		return of_ex_Latch;
	}

	public EX_MA_Latch getEX_MA_Latch() {
		return ex_ma_Latch;
	}

	public MA_RW_Latch getMA_RW_Latch() {
		return ma_rw_Latch;
	}
	
	// Getters of the units
	public InstructionFetch getIF_Unit() {
		return IF_Unit;
	}
	
	public OperandFetch getOF_Unit() {
		return OF_Unit;
	}
	
	public Execute getEX_Unit() {
		return EX_Unit;
	}
	
	public MemoryAccess getMA_Unit() {
		return MA_Unit;
	}
	
	public RegisterWrite getRW_Unit() {
		return RW_Unit;
	}
	
	// Getters of caches
	public Cache getL1iCache() {
		return l1iCache;
	}
	
	public Cache getL1dCache() {
		return l1dCache;
	}
}
