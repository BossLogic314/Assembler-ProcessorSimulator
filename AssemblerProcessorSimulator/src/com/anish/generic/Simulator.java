package com.anish.generic;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.anish.generic.Event.EventType;
import com.anish.memory.RegisterFile;
import com.anish.processor.Clock;
import com.anish.processor.Processor;

public class Simulator {

	private static Processor processor;
	private static EventQueue eventQueue;
	private static boolean simulationComplete;
	
	// Sets up the simulator
	public static void setupSimulator() {
		processor = new Processor();
		eventQueue = new EventQueue();
		simulationComplete = false;
	}
	
	// Returns the instance of the processor
	public static Processor getProcessor() {
		return processor;
	}
	
	// Returns the instance of the event queue
	public static EventQueue getEventQueue() {
		return eventQueue;
	}
	
	// Sets the boolean variable
	public static void setSimulationComplete(boolean simulationCompleteSetter) {
		simulationComplete = simulationCompleteSetter;
	}
	
	// Loads the program and sets up the default orientation of the address space layout
	public static void loadProgram(String outFile) throws IOException {
		
		FileInputStream fis = new FileInputStream(outFile);
		DataInputStream dis = new DataInputStream(fis);
		
		// If there is nothing in the file to read from
		if (dis.available() <= 0) {
			Errors.printFileContentError();
		}
		
		// Referencing to the register file
		RegisterFile registerFile = processor.getRegisterFile();
		
		// Reading the number of static variables in the file
		int numOfStaticVariables = dis.readInt();
		
		// To store data into the memory
		int addressCounter = 0;
		
		// Reading all the static variables
		for (int i = 0; i < numOfStaticVariables; ++i) {
			int value = dis.readInt();
			
			// Inserting data into the main memory
			registerFile.getMainMemory().setAddressValue(addressCounter++, value);
		}
		
		// Setting the program counter to the first instruction to read by IF unit
		registerFile.setProgramCounter(addressCounter);
		
		// Reading all the instructions and inserting them into the memory
		while(dis.available() > 0) {
			
			// Reading the instruction code
			int instructionCode = dis.readInt();
			
			// Inserting the instruction code into the memory
			registerFile.getMainMemory().setAddressValue(addressCounter++, instructionCode);
		}
		
		// This holds the value of the location beyond the last instruction
		registerFile.setLastInstructionCounter(addressCounter - 1);
		
		// Stack pointer and frame pointer
		registerFile.setRegisterValue(1, addressCounter);
		registerFile.setRegisterValue(2, addressCounter);
	}
	
	// Performs simulation
	public static void simulate() {
		
		// Keep simulating until completed
		while (true) {
			processor.getRW_Unit().performRW();
			
			// If the simulation is completed, break out of the loop
			if (simulationComplete)
				break;
			
			processor.getMA_Unit().performMA();
			eventQueue.processEvents();
			processor.getEX_Unit().performEX();
			processor.getOF_Unit().performOF();
			processor.getIF_Unit().performIF();
			
			// Incrementing the time
			Clock.addTime(1);
		}
		
		// To store the list of events in the event queue even after the execution is complete
		ArrayList<Event> listOfEvents = new ArrayList<Event>();
		
		// Inserting all the events into the list
		while(!eventQueue.isEventQueueEmpty())
			listOfEvents.add(eventQueue.popEvent());
		
		// Checking all the events
		for (Event event : listOfEvents) {
			
			// Adding all the memory write events
			if (event.getEventType() == EventType.MemoryWrite)
				eventQueue.addEvent(event);
		}
		
		// Waiting until the memory is updated
		while(!eventQueue.isEventQueueEmpty()) {
			eventQueue.processEvents();
			
			Clock.addTime(1);
		}
	}
	
	// Prints the data stored in the registers and in the main memory
	public static void printData() {
		
		// Reference to the register file
		RegisterFile registerFile = processor.getRegisterFile();
		
		int numberOfRegisters = registerFile.getNumberOfRegisters();
		
		System.out.println("Register data -");
		
		// Printing the values in the registers
		for (int i = 0; i < numberOfRegisters;++i)
			System.out.println("x" + i + " :" + registerFile.getRegisterValue(i));
		
		int mainMemorySize = registerFile.getMainMemory().getMemorySize();
		
		System.out.println("\nMain memory data -");
		
		// Printing the data in the main memory
		for (int i = 0; i < mainMemorySize; ++i)
			System.out.println(i + " : " + registerFile.getMainMemory().getAddressValue(i));
	}
}
