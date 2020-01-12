package zever;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 * This program reads power generation information from Alan's ZeverSolar inverter and
 * posts it to PVOutput.org.
 */

public class ZeverInverterToPVOutput {
	private static final Logger logger = LogManager.getLogger(ZeverInverterToPVOutput.class);
	private static Properties props = new Properties();
	private static final String ZeverString = "1\n" + "1\n" + "EAB96173XXXXXXX\n" + "NXVWWXSRRT7XXXXXXX\n" + "M11\n"
			+ "16B21-663R+16B21-XXXXXX\n" + "09:38 28/07/2017\n" + // will ignore this and just use current time
			"OK\n" + "1\n" + "SX000660117XXXXXXXXX\n" + "2265\n" + // Assume this is watt hours)
			"2.9\n" + // This needs to become 2.02 apparently
			"OK\n" + "Error\n";

	public static void main(String[] args) {
		try (InputStream input = new FileInputStream("src\\main\\resources\\properties.txt")) {
			props.load(input);
		} catch (IOException e) {
			logger.fatal(e.getMessage());
			System.exit(1);
		}
		logger.info("Inverter IP: " + props.getProperty("InverterURL"));
		Outputs p = new Outputs();
		p.load();
		p.updateOutputsWithInverterResponse(ZeverString);
	}
}
