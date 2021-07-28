package com.anish.generic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import com.anish.generic.Instruction.InstructionType;
import com.anish.generic.Instruction.OperationType;
import com.anish.generic.Operand.OperandType;

public class Parser {

	private static ArrayList<Integer> listOfStaticData;
	private static Hashtable<String, Integer> symbolTable;
	private static Hashtable<String, Integer> functionTable;
	private static Hashtable<String, Integer> functionCheckTable;
	private static ArrayList<String> listOfInputStrings;
	private static ArrayList<Instruction> listOfInstructions;
	private static ArrayList<String> listOfInstructionCodes;
	private static ArrayList<FunctionParameter> listOfFunctionParameters;
	private static ArrayList<String> listOfFunctionNames;
	
	// Sets up everything for the execution
	public static void setupParser() {
		
		listOfStaticData = new ArrayList<Integer>();
		symbolTable = new Hashtable<String, Integer>();
		functionTable = new Hashtable<String, Integer>();
		functionCheckTable = new Hashtable<String, Integer>();
		listOfInputStrings = new ArrayList<String>();
		listOfInstructions = new ArrayList<Instruction>();
		listOfInstructionCodes = new ArrayList<String>();
		listOfFunctionParameters = new ArrayList<FunctionParameter>();
		listOfFunctionNames = new ArrayList<String>();
	}
	
	// Returns whether the input string is a function declaration or not
	private static boolean isFunction(String inputString) {
		
		// A function definition must not begin with a '\t'
		if (inputString.charAt(0) == '\t')
			return false;
		
		// Splitting the command to check whether it is a label or a function definition
		String[] listOfData = inputString.split(" ", 2);
		
		if (listOfData.length == 1)
			return false;
		
		return true;
	}
	
	// Returns the name of the function
	private static String getFunctionName(String inputString) {
		
		// Splitting the command to check whether it is a label or a function definition
		String[] listOfData = inputString.split(" ", 2);
		
		// The name of the function is stored in the 1st entry
		String functionName = listOfData[0];
		
		return functionName;
	}
	
	// This function parses the assembly code in the input file into byte code
	@SuppressWarnings("deprecation")
	public static void parseProgram(String inputFile) throws IOException {
		
		FileInputStream fis = new FileInputStream(inputFile);
		
		int counter = 0;
		
		// try-with-resource block
		try (DataInputStream dis = new DataInputStream(fis)) {
		
			// Reading the first line of the input file
			String inputStringStatic = dis.readLine();
			
			// If the tag is missing
			if (inputStringStatic.compareTo("\t.data") != 0)
				Errors.printSyntaxError();
			
			// Keep reading the next lines
			while (dis.available() > 0) {
				inputStringStatic = dis.readLine();
				
				// If all the static data is read
				if (inputStringStatic.compareTo("\t.text") == 0)
					break;
				
				// If an integer data is read
				if (inputStringStatic.charAt(0) == '\t') {
					
					// Removing leading and trailing spaces
					inputStringStatic = inputStringStatic.trim();
					
					// Getting the value
					int value = Integer.parseInt(inputStringStatic);
					
					listOfStaticData.add(value);
					
					counter++;
					
					continue;
				}
				// If a label is encountered
					
				// Length of the label
				int length = inputStringStatic.length();
	
				inputStringStatic = inputStringStatic.substring(0, length - 1);
				symbolTable.put(inputStringStatic, counter);
			}
			
			// Reading all the lines to find out the functions present in the input file
			while (dis.available() > 0) {
				
				String inputString = dis.readLine();
				
				// Adding the input string to the list of input strings
				listOfInputStrings.add(inputString);
				
				// If the string not is a function declaration
				if (!isFunction(inputString))
					continue;
				
				// If the string is a function declaration
				String functionName = getFunctionName(inputString);
				
				// Inserting into the hash table
				functionCheckTable.put(functionName, 1);
			}
		}
		
		// Storing the address of the first instruction
		int instructionAddress = counter;
		
		// Reading all the input strings again
		for (String inputString : listOfInputStrings) {
			
			// For a closing brace of a function
			if (inputString.compareTo("}") == 0) {
				listOfInstructionCodes.add(inputString);
				continue;
			}
			
			// If the string is a normal instruction
			if (inputString.charAt(0) == '\t') {
				
				listOfInstructionCodes.add(inputString);
				
				// Removing all the leading and trailing spaces
				inputString = inputString.trim();
				
				// Splitting the command
				String[] listOfData = inputString.split(" ", 2);
				
				// For a 'push' instruction
				if (listOfData[0].compareTo("push") == 0) {
					counter += 2;
					continue;
				}
				
				// For a 'ret' instruction
				if (listOfData[0].compareTo("ret") == 0) {
					counter += 5;
					continue;
				}
				
				// For a jump instruction
				if (listOfData[0].compareTo("jmp") == 0) {
					
					String functionName = listOfData[1];
					
					// If it is a call to a function
					if (functionCheckTable.get(functionName) != null) {
						counter += 7;
						continue;
					}
				}
				
				// If it is a normal instruction without any abnormalities
				counter++;
				
				continue;
			}
			
			// Splitting the command
			String[] listOfData = inputString.split(" ", 2);
			
			// Length of the label or function definition
			int length = inputString.length();
			
			// If the line read is a label
			if (!isFunction(inputString)) {
				
				// Removing the ':' character from the label
				inputString = inputString.substring(0, length - 1);
				symbolTable.put(inputString, counter);
				
				continue;
			}
			
			// If the control flow reaches here, the line read is a function definition
			
			// The name of the function is stored in the 1st entry
			String functionName = listOfData[0];
			functionTable.put(functionName, counter);
			
			// Adding this instruction also, to detect which function the parameters belong to
			listOfInstructionCodes.add(inputString);
			
			// Storing the string of the parameters
			String parametersString = listOfData[1];
			
			// Storing the length of the parameters string
			int lengthOfParametersString = parametersString.length();
			
			// If no arguments are sent to this function
			if (lengthOfParametersString == 3)
				continue;
			
			// Removing the opening and the closing braces
			parametersString = parametersString.substring(1, lengthOfParametersString - 2);
			
			// Splitting all the parameters
			String[] parametersOfFunction = parametersString.split(" ");
			
			// Storing the number of parameters
			int numberOfParameters = parametersOfFunction.length;
			
			// Adding all the arguments to the list of function parameters
			for (int i = 0; i < numberOfParameters; ++i) {
				
				// Extracting the parameter string and removing all the leading and trailing spaces
				String parameterString = parametersOfFunction[i].trim();
				
				// Storing the length of the parameter string
				int lengthOfParameterString = parameterString.length();
				
				// Removing the ',' at the end, if present
				if (parameterString.charAt(lengthOfParameterString - 1) == ',')
					parameterString = parameterString.substring(0, lengthOfParameterString - 1);
				
				// Adding this parameter to the list of function parameters
				listOfFunctionParameters.add(new FunctionParameter(functionName, parameterString, -2 - numberOfParameters + i));
			}
		}
		
		// Parsing all the instructions
		for (String inputString : listOfInstructionCodes) {
			
			// Getting the parsed instruction
			Instruction instruction = parseInstruction(inputString);
			
			// Nothing to do if a function definition was stored in the input string
			if (instruction == null)
				continue;
			
			// Setting the program counter of the instruction
			instruction.setProgramCounter(instructionAddress);
			
			// Storing the type of the operation
			OperationType operationType = instruction.getOperationType();
			
			// If the operation is 'push', it has to be split into two instructions
			if (operationType == OperationType.push) {
				
				// To push the value onto the stack
				Instruction instrStore = getStoreInstruction(instruction.getDest().getValue(), 0, 1);
				instrStore.setProgramCounter(instructionAddress++);
				
				// To update the stack pointer
				Instruction instrAddi = getAddiInstruction(1, 1, 1);
				instrAddi.setProgramCounter(instructionAddress++);
				
				// Adding both the instructions
				listOfInstructions.add(instrStore);
				listOfInstructions.add(instrAddi);
				
				continue;
			}
			
			// If the operation is 'jmp' to a function definition, it has to be split into two instructions
			if (operationType == OperationType.jmp && instruction.getSource2().getOperandType() == OperandType.Function) {
				
				int returnAddress = instructionAddress + 7;
				
				// To store the previous location of the frame pointer
				Instruction instrPrevFrameValueStore = getStoreInstruction(2, 0, 1);
				instrPrevFrameValueStore.setProgramCounter(instructionAddress++);
				
				// To update the stack pointer
				Instruction instrStackUpdate1 = getAddiInstruction(1, 1, 1);
				instrStackUpdate1.setProgramCounter(instructionAddress++);
				
				// To store the return address value in x3
				Instruction instrReturnValueStore= getAddiInstruction(0, returnAddress, 3);
				instrReturnValueStore.setProgramCounter(instructionAddress++);
				
				// To push the return address value onto the stack
				Instruction instrReturnValuePush = getStoreInstruction(3, 0, 1);
				instrReturnValuePush.setProgramCounter(instructionAddress++);
				
				// To update the stack pointer
				Instruction instrStackUpdate2 = getAddiInstruction(1, 1, 1);
				instrStackUpdate2.setProgramCounter(instructionAddress++);
				
				// To update the frame pointer
				Instruction instrFrameUpdate = getAddiInstruction(1, 0, 2);
				instrFrameUpdate.setProgramCounter(instructionAddress++);
				
				instruction.setProgramCounter(instructionAddress++);
				
				// Adding all the instructions
				listOfInstructions.add(instrPrevFrameValueStore);
				listOfInstructions.add(instrStackUpdate1);
				listOfInstructions.add(instrReturnValueStore);
				listOfInstructions.add(instrReturnValuePush);
				listOfInstructions.add(instrStackUpdate2);
				listOfInstructions.add(instrFrameUpdate);
				listOfInstructions.add(instruction);
				
				continue;
			}
			
			// For a 'ret' operation
			if (operationType == OperationType.ret) {
				
				// To store the return address value in x3
				Instruction instrReturnAddressStore = getLoadInstruction(2, -1, 3);
				instrReturnAddressStore.setProgramCounter(instructionAddress++);
				
				// To store the previous value of the frame pointer in x4
				Instruction instrPrevFrameValueStore = getLoadInstruction(2, -2, 4);
				instrPrevFrameValueStore.setProgramCounter(instructionAddress++);
				
				// To update the frame pointer to its previous value
				Instruction instrFrameUpdate = getAddiInstruction(4, 0, 2);
				instrFrameUpdate.setProgramCounter(instructionAddress++);
				
				// To update the stack pointer to the value of the frame pointer
				Instruction instrStackUpdate = getAddiInstruction(4, 0, 1);
				instrStackUpdate.setProgramCounter(instructionAddress++);
				
				// To jump back to the address after which the function was called
				Instruction instrJump = getJumpInstruction(3, 0);
				instrJump.setProgramCounter(instructionAddress++);
				
				// Adding all the instructions
				listOfInstructions.add(instrReturnAddressStore);
				listOfInstructions.add(instrPrevFrameValueStore);
				listOfInstructions.add(instrFrameUpdate);
				listOfInstructions.add(instrStackUpdate);
				listOfInstructions.add(instrJump);
				
				continue;
			}
			
			// Adding the instruction obtained after being parsed if none of the above cases are matched
			listOfInstructions.add(instruction);
			instructionAddress++;
		}
	}
	
	// Decodes the instruction into an instance of 'Instruction'
	private static Instruction parseInstruction(String inputString) {
		
		// For a closing brace of a function
		if (inputString.compareTo("}") == 0) {
			
			// Length of the list of function names
			int size = listOfFunctionNames.size();
			
			// Removing the last function from the list of function names
			listOfFunctionNames.remove(size - 1);
			
			return null;
		}
		
		// To find whether the input string is a function definition
		boolean isFunctionDefinition = false;
		
		// If the input string is a function definition
		if (inputString.charAt(0) != '\t')
			isFunctionDefinition = true;
		
		// Discarding the leading and trailing spaces
		inputString = inputString.trim();
		
		// Storing all the data
		String[] listOfData = inputString.split(" ");
		
		// If the input string is a function definition
		if (isFunctionDefinition) {
			
			// Adding the name of the function to the list of function names
			listOfFunctionNames.add(listOfData[0]);
			return null;
		}
		
		Instruction instruction = new Instruction();

		// Getting the operation type of the instruction
		OperationType operationType = getOperationType(listOfData[0]);
		
		// Setting the operation type of the instruction
		instruction.setOperationType(operationType);
		
		// Setting the instruction type of the instruction
		instruction.setInstructionType(Instruction.findInstructionType(operationType));
		
		// All the operands are null for an 'end' instruction
		if (operationType == OperationType.end)
			return instruction;
		
		// The last function needs to be removed from the list of function names
		if (operationType == OperationType.ret)
			return instruction;
		
		// There is only an immediate operand for 'jmp' instruction
		if (operationType == OperationType.jmp) {
			
			instruction.setSource2(getOperand(listOfData[1]));
			return instruction;
		}
		
		// There is only source1 operand for 'push' instruction
		if (operationType == OperationType.push) {
			
			instruction.setDest(getOperand(listOfData[1]));
			return instruction;
		}
		
		// Finding out the operands and adding it to the instruction
		instruction.setSource1(getOperand(listOfData[1]));
		
		// For branch instructions
		if (operationType.ordinal() >= OperationType.beq.ordinal() && operationType.ordinal() <= OperationType.bgt.ordinal()) {
			
			instruction.setSource2(getOperand(listOfData[3]));
			instruction.setDest(getOperand(listOfData[2]));
		}
		// For arithmetic instructions
		else {
			instruction.setSource2(getOperand(listOfData[2]));
			instruction.setDest(getOperand(listOfData[3]));
		}
		
		return instruction;
	}
	
	// Returns an instance of 'Operand' by decoding the string
	private static Operand getOperand(String operandString) {
		
		Operand operand = new Operand();
		
		// Storing the length of the string
		int length = operandString.length();
		
		// Removing ',' from the end, if present
		if (operandString.charAt(length - 1) == ',') {
			operandString = operandString.substring(0, length - 1);
			length--;
		}
		
		// If the operand is a register
		if (operandString.charAt(0) == '%') {
			
			// The register number is contained in the form of a string
			String registerNumberString = operandString.substring(length - 2, length);
			
			// If the register number is a single digit
			if (registerNumberString.charAt(0) == 'x')
				registerNumberString = registerNumberString.substring(1);
			
			// Storing the register number as an integer
			int registerNumber = Integer.parseInt(registerNumberString);
			
			// Inserting the information into the operand
			operand.setOperandType(OperandType.Register);
			operand.setValue(registerNumber);
			
			return operand;
		}
		
		// If the operand is a label
		if (operandString.charAt(0) == '$') {
			
			// Storing the label in the form of a string
			String labelString = operandString.substring(1);
			
			// Getting the address from the hash table
			int address = symbolTable.get(labelString);
			
			// Inserting the information into the operand
			operand.setOperandType(OperandType.Label);
			operand.setValue(address);
			
			return operand;
		}
		
		// If the operand is a parameter
		if (operandString.charAt(0) == '#') {
			
			// Storing the length of the list of function names
			int size = listOfFunctionNames.size();
			
			// Finding the function parameter
			FunctionParameter functionParameter = getFunctionParameter(operandString, listOfFunctionNames.get(size - 1));
			
			// Inserting the information into the operand
			operand.setOperandType(OperandType.Parameter);
			operand.setValue(functionParameter.getParameterNumber());
			
			return operand;
		}
		
		// If the control flow reaches here, the operand can either be an immediate value, a label or a function name
		
		// For a label or a function call
		if ((operandString.charAt(0) >= 'a' && operandString.charAt(0) <= 'z') ||
				(operandString.charAt(0) >= 'A' && operandString.charAt(0) <= 'Z')) {
			
			// Getting the value from the symbol table
			Integer value = symbolTable.get(operandString);
			
			// If the value is not null, the operand is a label
			if (value != null) {
				
				// Inserting the information into the operand
				operand.setOperandType(OperandType.Label);
				operand.setValue(value);
				
				return operand;
			}
			
			// If the control flow reaches here, the operand is a function name
			
			// Inserting the information into the operand
			operand.setOperandType(OperandType.Function);
			operand.setValue(functionTable.get(operandString));
			
			return operand;
		}
		
		// If the control flow reaches here, the operand is an immediate value
		
		int value = Integer.parseInt(operandString);
		
		// Inserting the information into the operand
		operand.setOperandType(OperandType.Immediate);
		operand.setValue(value);
		
		return operand;
	}
	
	// Returns the function parameter from the list of function parameters
	private static FunctionParameter getFunctionParameter(String parameterName, String functionName) {
		
		// Iterating through the list of function parameters
		for (FunctionParameter functionParameter : listOfFunctionParameters) {
			
			// If the parameter is found
			if (functionParameter.getParameterName().compareTo(parameterName) == 0 &&
					functionParameter.getFunctionName().compareTo(functionName) == 0)
				return functionParameter;
		}
		
		// If the function parameter is not found
		return null;
	}
	
	// Returns the operation type
	private static OperationType getOperationType(String inputString) {
		
		if (inputString.compareTo("add") == 0)
			return OperationType.add;
		if (inputString.compareTo("addi") == 0)
			return OperationType.addi;
		if (inputString.compareTo("sub") == 0)
			return OperationType.sub;
		if (inputString.compareTo("subi") == 0)
			return OperationType.subi;
		if (inputString.compareTo("mul") == 0)
			return OperationType.mul;
		if (inputString.compareTo("muli") == 0)
			return OperationType.muli;
		if (inputString.compareTo("div") == 0)
			return OperationType.div;
		if (inputString.compareTo("divi") == 0)
			return OperationType.divi;
		if (inputString.compareTo("and") == 0)
			return OperationType.and;
		if (inputString.compareTo("andi") == 0)
			return OperationType.andi;
		if (inputString.compareTo("or") == 0)
			return OperationType.or;
		if (inputString.compareTo("ori") == 0)
			return OperationType.ori;
		if (inputString.compareTo("xor") == 0)
			return OperationType.xor;
		if (inputString.compareTo("xori") == 0)
			return OperationType.xori;
		if (inputString.compareTo("slt") == 0)
			return OperationType.slt;
		if (inputString.compareTo("slti") == 0)
			return OperationType.slti;
		if (inputString.compareTo("sll") == 0)
			return OperationType.sll;
		if (inputString.compareTo("slli") == 0)
			return OperationType.slli;
		if (inputString.compareTo("srl") == 0)
			return OperationType.srl;
		if (inputString.compareTo("srli") == 0)
			return OperationType.srli;
		if (inputString.compareTo("sra") == 0)
			return OperationType.sra;
		if (inputString.compareTo("srai") == 0)
			return OperationType.srai;
		if (inputString.compareTo("load") == 0)
			return OperationType.load;
		if (inputString.compareTo("store") == 0)
			return OperationType.store;
		if (inputString.compareTo("jmp") == 0)
			return OperationType.jmp;
		if (inputString.compareTo("beq") == 0)
			return OperationType.beq;
		if (inputString.compareTo("bne") == 0)
			return OperationType.bne;
		if (inputString.compareTo("blt") == 0)
			return OperationType.blt;
		if (inputString.compareTo("bgt") == 0)
			return OperationType.bgt;
		if (inputString.compareTo("end") == 0)
			return OperationType.end;
		if (inputString.compareTo("push") == 0)
			return OperationType.push;
		if (inputString.compareTo("ret") == 0)
			return OperationType.ret;
		
		// If the string matches no operation
		return null;
	}
	
	// Converts the parsed assembly code into byte code
	public static void assemble(String outputFile) throws IOException {
		
		FileOutputStream fos = new FileOutputStream(outputFile);
		
		// try-with-resource block
		try (DataOutputStream dos = new DataOutputStream(fos)) {
		
			// Storing the number of static variables
			int numberOfStaticVariables = listOfStaticData.size();
			
			// Writing the number of static variables into the output file
			dos.writeInt(numberOfStaticVariables);
			
			// Writing all the static variables into the output file
			for (Integer value : listOfStaticData)
				dos.writeInt(value);
			
			// Generating byte code for all the instructions
			for (Instruction instruction : listOfInstructions) {
				int instructionCode = generateInstructionCode(instruction);
				
				// Writing the instruction code into the output file
				dos.writeInt(instructionCode);
			}
		}
		
		fos.close();
	}
	
	// Converts instances of 'Instruction' into byte code and writes it into the output file
	private static int generateInstructionCode(Instruction instruction) {
		
		// Storing the type of the instruction
		InstructionType instructionType = instruction.getInstructionType();
		
		// Operation type of the instruction
		OperationType operationType = instruction.getOperationType();
		
		// Storing the operands of the instruction
		Operand source1 = instruction.getSource1();
		Operand source2 = instruction.getSource2();
		Operand dest = instruction.getDest();
		
		// The opcode in the string format
		String opCodeString = String.format("%5s", Integer.toBinaryString(operationType.ordinal())).replace(' ', '0');
		
		// Byte code as a string
		String byteCode = "";
		
		// To store all the values in string format
		String source1String, source2String, destString;
		
		// For an 'end' instruction
		if (operationType == OperationType.end) {
			
			String unusedBitsString = String.format("%27s", Integer.toBinaryString(0)).replace(' ', '0');
			
			// Concatenating all the operand codes to generate the byte code
			byteCode = byteCode.concat(opCodeString);
			byteCode = byteCode.concat(unusedBitsString);
		}
		
		// For a 'jmp' instruction
		else if (operationType == OperationType.jmp) {
			
			// The counter to jump
			int jumpValue = source2.getValue() - instruction.getProgramCounter();
			
			source2String = String.format("%22s", Integer.toBinaryString(jumpValue)).replace(' ', '0');
			
			if (dest == null)
				destString = String.format("%5s", Integer.toBinaryString(0)).replace(' ', '0');
			else
				destString = String.format("%5s", Integer.toBinaryString(dest.getValue())).replace(' ', '0');
			
			// For negative values, 32 bit strings are generated
			if (jumpValue < 0)
				source2String = source2String.substring(10);
			
			// Concatenating all the operand codes to generate the byte code
			byteCode = byteCode.concat(opCodeString);
			byteCode = byteCode.concat(destString);
			byteCode = byteCode.concat(source2String);
		}
		
		// For an R3 instruction
		else if (instructionType == InstructionType.R3) {
			
			source1String = String.format("%5s", Integer.toBinaryString(source1.getValue())).replace(' ', '0');
			source2String = String.format("%5s", Integer.toBinaryString(source2.getValue())).replace(' ', '0');
			destString = String.format("%5s", Integer.toBinaryString(dest.getValue())).replace(' ', '0');
			String unusedBitsString = String.format("%12s", Integer.toBinaryString(0)).replace(' ', '0');
			
			// Concatenating all the operand codes to generate the byte code
			byteCode = byteCode.concat(opCodeString);
			byteCode = byteCode.concat(source1String);
			byteCode = byteCode.concat(source2String);
			byteCode = byteCode.concat(destString);
			byteCode = byteCode.concat(unusedBitsString);
		}
		
		// For an R2I instruction
		else if (instructionType == InstructionType.R2I) {
			
			source1String = String.format("%5s", Integer.toBinaryString(source1.getValue())).replace(' ', '0');
			source2String = "";
			destString = String.format("%5s", Integer.toBinaryString(dest.getValue())).replace(' ', '0');
			
			// Determining the value of the immediate operand
			if (operationType.ordinal() >= OperationType.beq.ordinal() && operationType.ordinal() <= OperationType.bgt.ordinal()) {
				
				// The counter to jump
				int jumpValue = source2.getValue() - instruction.getProgramCounter();
				
				source2String = String.format("%17s", Integer.toBinaryString(jumpValue)).replace(' ', '0');
				
				// For negative values, 32 bit strings are generated
				if (jumpValue < 0)
					source2String = source2String.substring(10);
			}
			else {
				source2String = String.format("%17s", Integer.toBinaryString(source2.getValue())).replace(' ', '0');
				
				// For negative values, 32 bit strings are generated
				if (source2.getValue() < 0)
					source2String = source2String.substring(15);
			}
			
			// Concatenating all the operand codes to generate the byte code
			byteCode = byteCode.concat(opCodeString);
			byteCode = byteCode.concat(source1String);
			byteCode = byteCode.concat(destString);
			byteCode = byteCode.concat(source2String);
		}
		
		// Integer code to be written into the output file
		int instructionCode = Integer.parseUnsignedInt(byteCode, 2);
		return instructionCode;
	}
	
	// Returns a 'store' instruction with the necessary operands
	private static Instruction getStoreInstruction(int source1Val, int source2Val, int destVal) {
		
		Instruction instruction = new Instruction();
		
		// Creating a 'load' instruction
		instruction.setOperationType(OperationType.store);
		instruction.setInstructionType(InstructionType.R2I);
		
		// Operands of the instruction
		Operand source1 = new Operand();
		Operand source2 = new Operand();
		Operand dest = new Operand();
		
		source1.setOperandType(OperandType.Register);
		source1.setValue(source1Val);
		
		source2.setOperandType(OperandType.Immediate);
		source2.setValue(source2Val);
		
		dest.setOperandType(OperandType.Register);
		dest.setValue(destVal);
		
		// Setting the operands to the instruction
		instruction.setSource1(source1);
		instruction.setSource2(source2);
		instruction.setDest(dest);
		
		return instruction;
	}
	
	// Returns an 'addi' instruction with the necessary operands
	private static Instruction getAddiInstruction(int source1Val, int source2Val, int destVal) {
		
		Instruction instruction = new Instruction();
		
		// Creating a 'load' instruction
		instruction.setOperationType(OperationType.addi);
		instruction.setInstructionType(InstructionType.R2I);
		
		// Operands of the instruction
		Operand source1 = new Operand();
		Operand source2 = new Operand();
		Operand dest = new Operand();
		
		source1.setOperandType(OperandType.Register);
		source1.setValue(source1Val);
		
		source2.setOperandType(OperandType.Immediate);
		source2.setValue(source2Val);
		
		dest.setOperandType(OperandType.Register);
		dest.setValue(destVal);
		
		// Setting the operands to the instruction
		instruction.setSource1(source1);
		instruction.setSource2(source2);
		instruction.setDest(dest);
		
		return instruction;
	}
	
	// Returns a 'load' instruction with the necessary operands
	private static Instruction getLoadInstruction(int source1Val, int source2Val, int destVal) {
		
		Instruction instruction = new Instruction();
		
		// Creating a 'load' instruction
		instruction.setOperationType(OperationType.load);
		instruction.setInstructionType(InstructionType.R2I);
		
		// Operands of the instruction
		Operand source1 = new Operand();
		Operand source2 = new Operand();
		Operand dest = new Operand();
		
		source1.setOperandType(OperandType.Register);
		source1.setValue(source1Val);
		
		source2.setOperandType(OperandType.Immediate);
		source2.setValue(source2Val);
		
		dest.setOperandType(OperandType.Register);
		dest.setValue(destVal);
		
		// Setting the operands to the instruction
		instruction.setSource1(source1);
		instruction.setSource2(source2);
		instruction.setDest(dest);
		
		return instruction;
	}
	
	// Returns a 'jmp' instruction with the necessary operands
	private static Instruction getJumpInstruction(int destVal, int source2Val) {
		
		Instruction instruction = new Instruction();
		
		// Creating a 'load' instruction
		instruction.setOperationType(OperationType.jmp);
		instruction.setInstructionType(InstructionType.RI);
		
		// Operands of the instruction
		Operand source2 = new Operand();
		Operand dest = new Operand();
		
		source2.setOperandType(OperandType.Immediate);
		source2.setValue(source2Val);
		
		dest.setOperandType(OperandType.Register);
		dest.setValue(destVal);
		
		// Setting the operands to the instruction
		instruction.setSource2(source2);
		instruction.setDest(dest);
		
		return instruction;
	}
}
