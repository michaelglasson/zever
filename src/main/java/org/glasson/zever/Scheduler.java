package org.glasson.zever;

import java.time.LocalTime;

public class Scheduler {

	public static void main(String[] args) throws InterruptedException {
		while (true) {
			Output singleOutput = HttpWorker.collectFromInverter();
			OutputSet.addOutput(singleOutput);
			int hour = LocalTime.now().getHour();
			if (hour >= 18) { // Sleep from 1800
				HttpWorker.postOutput(OutputSet.getDaySummary());
				OutputSet.resetDaySummary();
				Thread.sleep(14 * 60 * 60 * 1000 - 1000); // Sleep until 0800
			}
			System.out.println("oneMinuteOutput: " + singleOutput.toCSV());
			if (LocalTime.now().getMinute() % 5 == 0) {
				HttpWorker.postStatus(OutputSet.getFiveMinuteSummary());
				System.out.println("Five minute summary: " + OutputSet.getFiveMinuteSummary().toCSV());
				System.out.println("Daily Summary: " + OutputSet.getDaySummary().toCSV());
			}
			Thread.sleep(millisecondsToNextMinute());
		}
	}

	public static int millisecondsToNextMinute() {
		LocalTime t = LocalTime.now();
		int millis = t.getSecond() * 1000 + t.getNano() / 1000000;
		return 60000 - millis;
	}
}
