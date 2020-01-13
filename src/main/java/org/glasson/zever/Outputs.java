package org.glasson.zever;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

public class Outputs {
	public static Map<String, Output> outputs = new TreeMap<>();
	private static final Properties props = new Properties();
	private static final String inverterResponse = "1\n" + "1\n" + "EAB96173XXXXXXX\n" + "NXVWWXSRRT7XXXXXXX\n"
			+ "M11\n" + "16B21-663R+16B21-XXXXXX\n" + "11:00 31/08/2018\n" + // will ignore this and just use current
																				// time
			"OK\n" + "1\n" + "SX000660117XXXXXXXXX\n" + "2300\n" + // Assume this is watt hours)
			"2.9\n" + // This needs to become 2.02 apparently
			"OK\n" + "Error\n";

	public Outputs() {
		try (InputStream input = new FileInputStream("properties.txt")) {
			props.load(input);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}

	/*
	 * This method reads power generation information from a ZeverSolar inverter and
	 * puts it into an output file. Each time it is run, it checks the peak output
	 * figure in the file against the output for the run and updates the peak output
	 * and peak time if necessary. The output file contains any outputs that have
	 * not yet been uploaded to PVOutput. In other words, the output file will have
	 * today's output and any previous days' outputs that have not yet been
	 * uploaded.
	 */
	public void collect() {
		if (!Files.exists(Paths.get(props.getProperty("LocalCacheFileName")))) {
			try {
				Files.createFile(Paths.get(props.getProperty("LocalCacheFileName")));
			} catch (IOException e) {
				System.out.println("Could not create output file. " + e.getMessage());
			}
		}
		try (Stream<String> s = Files.lines(Paths.get(props.getProperty("LocalCacheFileName")))) {
			s.forEach(l -> mapLine(l));
		} catch (IOException e) {
			System.out.println("Cannot read the output file. " + e.getMessage());
		}
		System.out.println("Inverter IP: " + props.getProperty("InverterURL"));
		updateOutputsWithInverterResponse(inverterResponse);
	}

	/*
	 * This writes the whole map to the outputs file, replacing whatever was
	 * previously in there.
	 */
	private void save() {
		try (PrintWriter pw = new PrintWriter(Paths.get(props.getProperty("LocalCacheFileName")).toFile())) {
			for (String s : outputs.keySet()) {
				pw.println(outputs.get(s).toCSV());
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Convert a line from the outputs file into an Output object. This is used when
	 * reading the output file into the outputs map. This must happen when the
	 * outputs file is updated.
	 */
	private void mapLine(String lineToMap) {
		String[] s = lineToMap.split(",");
		if (s.length != 5)
			System.out.println("Stored output in wrong format. It should have 5 parts, but has: " + s.length + "Content is: "
					+ lineToMap);
		Output o = new Output();
		o.date = s[0];
		o.generated = s[1];
		o.peak = s[3];
		o.peakTime = s[4];
		outputs.put(o.date, o);
	}

	/*
	 * This updates the output file, either adding a new line or updating the total
	 * generation and (if required) peak output and output time. It only updates the
	 * output for the current day. This prepares the output file for posting to
	 * PVOutput. The whole outputs file is overwritten with the current data from
	 * the outputs map when save() is executed.
	 */
	private void updateOutputsWithInverterResponse(String inverterResponse) {
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

	/*
	 * For each output in the collection, try to post it to PVOutput. Delete each
	 * output when it is posted successsfully. This means that the output file
	 * should only contain unposted data. Data should therefore only be written once
	 * per day after total day's generation, peak output and peak output time are
	 * known. If there is a failure, the output remains in the file and can be
	 * posted in the next run.
	 */
	public void postToPVOutput() {
		for (String s : outputs.keySet()) {
			if (doPost(outputs.get(s).toCSV())) {
				outputs.remove(s);
			}
		}
		save();
	}

	/*
	 * Post inverter output to PVOutput. This is called once for each output. The
	 * fields provided are date, generated, peak and peak time. API key is in the
	 * properties file. Inverter data is generated by the toCSV method of the Output
	 * class. The PVOutput API is a little ambiguous in that it does not specify
	 * that the data should be in a form.
	 */
	private boolean doPost(String inverterData) {
		try {
			Request req = Request.Post(props.getProperty("PVOutput.URL"));
			req.version(HttpVersion.HTTP_1_1).connectTimeout(1000).socketTimeout(1000)
					.addHeader("X-Pvoutput-Apikey", props.getProperty("PVOutput.APIKey"))
					.addHeader("X-Pvoutput-SystemId", props.getProperty("PVOutput.SystemId"))
					.bodyForm(Form.form().add("data", inverterData).build());
			HttpResponse response = req.execute().returnResponse();
			System.out.println(response.getStatusLine().toString());
			if (response.getStatusLine().getStatusCode() == 200)
				return true;
		} catch (ClientProtocolException e) {
			System.out.println(e.getStackTrace());
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		}
		return false;
	}
}