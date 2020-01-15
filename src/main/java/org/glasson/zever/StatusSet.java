package org.glasson.zever;

import java.util.LinkedHashSet;
import java.util.Set;

public class StatusSet {
	private static Set<Output> outputs = new LinkedHashSet<>();
	private static String thisTimeWindow; // Multiples of 5 minutes like "09:40"
	
	public static boolean timeWindowHasPassed(String toCheck) {
		toCheck = keyForTime(toCheck);
		return !thisTimeWindow.equals(toCheck);
	}
	
	public static void addOutput(Output toAdd) {
		if (!thisTimeWindow.equals(keyForTime(toAdd.time))) {
			outputs.clear();
			thisTimeWindow = keyForTime(toAdd.time);
		}
		outputs.add(toAdd);
	}
	
	public static Output getSummaryForThisTimeWindow() {
		Output out = new Output();
		if (outputs.size() == 0) return out;
		int p = 0;
		for (Output o : outputs) {
			p += Integer.parseInt(o.power);
			out = o;
		}
		out.power = String.valueOf(p / outputs.size());
		out.time = thisTimeWindow;
		return out; 
	}
	
	public static String keyForTime(String time) {
		// Time is like "09:38" and should map to "09:40"
		int t = Integer.parseInt(time.substring(time.indexOf(":") + 1));
		t = (t + 4) / 5 * 5;
		String s = String.format("%02d", t);
		return time.replaceFirst(":\\d\\d", ":" + s);
	}
}


