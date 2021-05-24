package com.anish.processor;

public class Clock {

	private static int time;
	
	// Constructor
	public Clock() {
		time = 0;
	}
	
	// Adds the time on the clock
	public static void addTime(int counter) {
		time += counter;
	}
	
	// Returns the time on the clock
	public static int getTime() {
		return time;
	}
}
