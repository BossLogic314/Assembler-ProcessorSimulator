package com.anish.generic;

import java.util.PriorityQueue;

import com.anish.processor.Clock;

public class EventQueue {

	private PriorityQueue<Event> priorityQueue;
	
	// Constructor
	public EventQueue() {
		priorityQueue = new PriorityQueue<Event>(new EventComparator());
	}
	
	// Returns the event queue
	public PriorityQueue<Event> getEventQueue(){
		return priorityQueue;
	}
	
	// Returns whether the priority queue is empty
	public boolean isEventQueueEmpty() {
		return priorityQueue.isEmpty() == true;
	}
	
	// Adds an event to the event queue
	public void addEvent(Event event) {
		priorityQueue.add(event);
	}
	
	// Returns the top-most event from the event queue
	public Event popEvent() {
		
		// If the event queue is empty
		if (priorityQueue.isEmpty())
			return null;
		
		return priorityQueue.poll();
	}
	
	// Executes events whose firing time is due
	public void processEvents() {
		
		while(!priorityQueue.isEmpty()) {
			
			// Peeking into the topmost event in the queue
			Event topEvent = priorityQueue.peek();
			
			// Getting the firing time of the event
			int fireTime = topEvent.getFireTime();
			
			// If the firing of the event is due
			if (fireTime <= Clock.getTime()) {
				
				// Invoking the instance of the processing handler to handle this event
				Handler processingHandler = topEvent.getProcessingHandler();
				processingHandler.handleEvent(topEvent);
				
				// Removing the top-most event from the event queue
				priorityQueue.poll();
				
				continue;
			}
			
			break;
		}
	}
}
