package zever;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Outputs {
	public Map<String, Output> outputs = new TreeMap<>();
	private static final Logger logger = LogManager.getLogger(Outputs.class);

	public void load() {
		if (!Files.exists(Paths.get("output"))) return;
		try (Stream<String> s = Files.lines(Paths.get("output"))) {
			s.forEach(l -> mapLine(l));
		} catch (IOException e) {
			logger.error("Cannot read the output file. " + e.getMessage());
		}
	}

	public void save() {
		try (PrintWriter pw = new PrintWriter(Paths.get("output").toFile())) {
			for (String s : outputs.keySet()) {
				pw.println(outputs.get(s).toCSV());
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		}
	}

	public void mapLine(String lineToMap) {
		String[] s = lineToMap.split(",");
		if (s.length != 5)
			logger.error("Stored output in wrong format. It should have 5 parts, but has: " + s.length +
					"Content is: " + lineToMap);
		Output o = new Output();
		o.date = s[0];
		o.generated = s[1];
		o.peak = s[2];
		o.peakTime = s[3];
		outputs.put(o.date, o);
	}

	public void updateOutputsWithInverterResponse(String inverterResponse) {
		Output o = new Output(inverterResponse);
		if (outputs.containsKey(o.date)) {
			if (Integer.parseInt(outputs.get(o.date).peak) < Integer.parseInt(o.peak)) {
				outputs.put(o.date, o);
			} else {
				outputs.get(o.date).generated = o.generated;
			}
		} else {
			outputs.put(o.date, o);
		}
		save();
	}
}
