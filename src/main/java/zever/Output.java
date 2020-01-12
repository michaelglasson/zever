package zever;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Output {
	public String date;           // yyyymmdd
	public String generated;      // watt hours generated so far today
	public String peak;           // watts peak power generated today 
	public String peakTime;       // hh:mm time at which peak power was generated
	
	private static final Logger logger = LogManager.getLogger(Output.class);
	
	public Output() {}
	
	public Output(String inverterResponse) {
		String[] parts = inverterResponse.split("\n");
		if (parts.length != 14) logger.error("Inverter response did not have 14 lines. Actually had: " + parts.length);
		DateTimeFormatter zeverDateTime = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
		LocalDateTime d = LocalDateTime.now();
		try {
			d = LocalDateTime.parse(parts[6], zeverDateTime);
		} catch (DateTimeParseException e) {
			logger.error("Could not parse time and date from inverter. Using current time and date. " + e.getMessage());
		}
		date = d.format(DateTimeFormatter.BASIC_ISO_DATE);
		peakTime = d.format(DateTimeFormatter.ofPattern("HH:mm"));
		generated = String.valueOf((int)(Float.valueOf(parts[11].replaceFirst("\\.(\\d)$", ".0$1")) * 1000));
		peak = parts[10];
	}

	public String toCSV() {
		return String.format("%s,%s,,%s,%s", date, generated, peak, peakTime);
	}
}
