package org.glasson.zever;

public class OutputSet {
	private static Output[] oneMinuteOutputs = new Output[5];
	private static Output daySummary = new Output();
	private static Output fiveMinuteSummary = new Output();
	
	public static void addOutput(Output toAdd) {
		oneMinuteOutputs[arrayIndexForTime(toAdd.time)] = toAdd;
		daySummary.date = toAdd.date;
		daySummary.generated = toAdd.generated;
		if (Integer.parseInt(daySummary.power) < Integer.parseInt(toAdd.power)) {
			daySummary.power = toAdd.power;
			daySummary.time = toAdd.time;
		}
	}
	
	public static Output getFiveMinuteSummary() {
		int p = 0;
		int n = 5; // Use this when array is partially filled
		for (int i = 0 ; i < 5 ; i++) {
			if (oneMinuteOutputs[i] == null) n--;
			else p += Integer.parseInt(oneMinuteOutputs[i].power);
		}
		fiveMinuteSummary.power = String.valueOf(p / n);
		fiveMinuteSummary.date = oneMinuteOutputs[0].date;
		fiveMinuteSummary.time = oneMinuteOutputs[0].time;
		fiveMinuteSummary.generated = oneMinuteOutputs[0].generated;
		return fiveMinuteSummary; 
	}
	
	public static Output getDaySummary() {
		return daySummary;
	}
	
	public static void resetDaySummary() {
		daySummary = new Output();
	}
	
	// Return 0,1,2,3,4 given a time, e.g. 9:38 -> 3
	public static int arrayIndexForTime(String time) {
		int t = Integer.parseInt(time.substring(time.indexOf(":") + 2));
		return t < 5 ? t : t-5;
	}
}
