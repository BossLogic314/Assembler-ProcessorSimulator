package com.anish.generic;

public class Event {

	public enum EventType {MemoryRead, MemoryResponse, MemoryWrite, PerformExecution, ExecutionComplete;}
	
	private int fireTime;
	private EventType eventType;
	
	private Handler requestingHandler;
	private Handler processingHandler;
	
	// Constructor
	public Event(int fireTime, EventType eventType, Handler requestingHandler, Handler processingHandler) {
		this.fireTime = fireTime;
		this.eventType = eventType;
		this.requestingHandler = requestingHandler;
		this.processingHandler = processingHandler;
	}
	
	// Sets the fire time of the event
	public void setFireTime(int fireTime) {
		this.fireTime = fireTime;
	}

	// Getters
	public int getFireTime() {
		return fireTime;
	}
	
	public EventType getEventType() {
		return eventType;
	}

	public Handler getRequestingHandler() {
		return requestingHandler;
	}

	public Handler getProcessingHandler() {
		return processingHandler;
	}
}
