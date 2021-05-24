package com.anish.generic;

public class FunctionParameter {

	private String functionName;
	private String parameterName;
	private int parameterNumber;
	
	// Constructor
	public FunctionParameter(String functionName, String parameterName, int parameterNumber) {
		this.functionName = functionName;
		this.parameterName = parameterName;
		this.parameterNumber = parameterNumber;
	}
	
	// Getters and setters
	public String getFunctionName() {
		return functionName;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public int getParameterNumber() {
		return parameterNumber;
	}
}
