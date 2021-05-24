package com.anish.generic;

public class Errors {

	// Exits the code if an error in the command line arguments is detected
	public static void printArgumentsError() {
		System.out.println("Invalid command line arguments provided!");
		System.exit(1);
	}
	
	// Error in the syntax of the assembly file
	public static void printSyntaxError() {
		System.out.println("Invalid syntax in the input file");
		System.exit(1);
	}
	
	// Exits the code if an error in the input file is detected
	public static void printFileContentError() {
		System.out.println("Invalid contents provided in the file!");
		System.exit(1);
	}
	
	// When a number is shifter by more than 32 bits
	public static void printShiftingError() {
		System.out.println("A number can be shifted by a maximum of 31 bits and a minimum of 0 bits");
		System.exit(1);
	}
}
