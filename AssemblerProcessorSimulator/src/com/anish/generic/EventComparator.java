package com.anish.generic;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {

	@Override
	// The comparator function
	public int compare(Event event1, Event event2) {
		
		int fireTime1 = event1.getFireTime();
		int fireTime2 = event2.getFireTime();
		
		// Returning the comparing value
		if (fireTime1 > fireTime2)
			return 1;
		
		else if(fireTime1 < fireTime2)
			return -1;
		
		else
			return 0;
	}
}
