package org.glasson.zever;

import java.time.LocalTime;

public class Scheduler {

	public static void main(String[] args) throws InterruptedException {
		Outputs o = new Outputs();
		Output singleOutput;
		while (true) {
			singleOutput = new Output(Collector.collect());
			int hour = LocalTime.now().getHour();
			if (hour >= 18) { // Sleep from 1800
				o.
				
				Thread.sleep(14 * 60 * 60 * 1000 - 1000); // Sleep until 0800
			}
			Thread.sleep(millisecondsToNextMinute());
			
			// If not end of day, just collect and update status
			o.collect();
			
			
			
			if (LocalTime.now().getMinute() % 5 == 0)
				o.postToPVOutput();
		}
	}

	public static int millisecondsToNextMinute() {
		LocalTime t = LocalTime.now();
		int millis = t.getSecond() * 1000 + t.getNano() / 1000000;
		return 60000 - millis;
	}
}
