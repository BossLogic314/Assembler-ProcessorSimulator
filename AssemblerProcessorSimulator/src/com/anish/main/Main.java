package com.anish.main;

import java.io.IOException;

import com.anish.generic.Errors;
import com.anish.generic.Parser;
import com.anish.generic.Simulator;

public class Main {

	public static void main(String args[]) throws IOException {
		
		// If invalid command line arguments are detected
		if (args.length != 2)
			Errors.printArgumentsError();
		
		// Setting up the default orientation and parsing the assembly code
		Parser.setupParser();
		Parser.parseProgram(args[0]);
		
		// Converting the parsed assembly instructions into byte code
		Parser.assemble(args[1]);
		
		// Setting up the default orientation and simulating the byte code
		Simulator.setupSimulator();
		Simulator.loadProgram(args[1]);
		Simulator.simulate();
		
		// Printing all the data
		Simulator.printData();
	}
}
