package org.glasson.zever;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class StatusSet {
	Map<String, Set<Output>> statusSet = new TreeMap<>();
	Set<Output> outputs = new HashSet<>();
	private static String thisTimeWindow;
	private static String newTimeWindow;
	// Make the key like 0930
	public static boolean timeWindowHasPassed(String toCheck) {
		toCheck = keyForTime(toCheck);
		return !thisTimeWindow.equals(toCheck);
	}
	
	public void addOutput(Output toAdd) {
		newTimeWindow = keyForTime(toAdd.time);
		if (!thisTimeWindow.equals(newTimeWindow)) {
			outputs.clear();
		}
		outputs.add(toAdd);
	}
	
	public void addStatus(Output o) {
	}
	
	public static String keyForTime(String time) {
		// Time is like "09:38" and should map to "09:40"
		int t = Integer.parseInt(time.substring(time.indexOf(":") + 1));
		t = (t + 4) / 5 * 5;
		String s = String.format("%02d", t);
		return time.replaceAll(":\\d\\d", ":" + s);
	}
}


