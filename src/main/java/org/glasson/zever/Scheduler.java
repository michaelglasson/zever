package org.glasson.zever;

import java.time.LocalTime;

public class Scheduler {

	public static void main(String[] args) throws InterruptedException {
		Outputs o = new Outputs();
		while (true) {
			int hour = LocalTime.now().getHour();
			if (hour < 19 && hour > 8) {
				Thread.sleep(millisecondsToNextMinute());
				o.collect();
				if (LocalTime.now().getMinute() % 5 == 0)
					o.postToPVOutput();
				System.out.println("Should be an even minute: " + LocalTime.now());
			}
		}
	}

	public static int millisecondsToNextMinute() {
		LocalTime t = LocalTime.now();
		int millis = t.getSecond() * 1000 + t.getNano() / 1000000;
		return 60000 - millis;
	}
}
