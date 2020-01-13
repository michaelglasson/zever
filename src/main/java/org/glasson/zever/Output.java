package org.glasson.zever;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Output {
	public String date;           // yyyymmdd
	public String generated;      // watt hours generated so far today
	public String peak;           // watts peak power generated today 
	public String peakTime;       // hh:mm time at which peak power was generated
	
	public Output() {} // Default constructor to allow creation of blank Output.

	/*
	 * This constructor takes an inverter response from the ZeverSolar inverter's home.cgi page and
	 * fills the fields of this instance. It needs to adjust for the Zever error in the 
	 * total generation for the day. "2.2" becomes "2.02", while "2.20" stays the same.
	 * See smeyn and other discussion on this. We also need to convert it from KWH to WH.
	 * The date time field in the Zever response is like "09:30 15/08/1957".
	 */
	public Output(String inverterResponse) {
		String[] parts = inverterResponse.split("\n");
		if (parts.length != 14) System.out.println("Inverter response did not have 14 lines. Actually had: " + parts.length);
		DateTimeFormatter zeverDateTime = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
		LocalDateTime d = LocalDateTime.now();
		try {
			d = LocalDateTime.parse(parts[6], zeverDateTime);
		} catch (DateTimeParseException e) {
			System.out.println("Could not parse time and date from inverter. Using current time and date. " + e.getMessage());
		}
		date = d.format(DateTimeFormatter.BASIC_ISO_DATE);
		peakTime = d.format(DateTimeFormatter.ofPattern("HH:mm"));
		generated = String.valueOf((int)(Float.valueOf(parts[11].replaceFirst("\\.(\\d)$", ".0$1")) * 1000));
		peak = parts[10];
	}

	/*
	 * Note the empty field to align with the PVOutput data format. See the PVOutput API
	 * documentation for details.
	 */
	public String toCSV() {
		return String.format("%s,%s,,%s,%s", date, generated, peak, peakTime);
	}
}
