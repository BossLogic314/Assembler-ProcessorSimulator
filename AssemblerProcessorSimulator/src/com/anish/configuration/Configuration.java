package com.anish.configuration;

public class Configuration {

	private static final int mainMemoryLatency = 40;
	private static final int l1iCacheLatency = 2;
	private static final int l1dCacheLatency = 2;
	
	private static final int additionLatency = 3;
	private static final int multiplicationLatency = 4;
	private static final int divisionLatency = 5;
	private static final int ALULatency = 2;
	
	// Getters
	public static int getMainmemorylatency() {
		return mainMemoryLatency;
	}
	public static int getL1iCacheLatency() {
		return l1iCacheLatency;
	}
	public static int getL1dCacheLatency() {
		return l1dCacheLatency;
	}
	public static int getAdditionlatency() {
		return additionLatency;
	}
	public static int getMultiplicationlatency() {
		return multiplicationLatency;
	}
	public static int getDivisionlatency() {
		return divisionLatency;
	}
	public static int getALULatency() {
		return ALULatency;
	}
}
