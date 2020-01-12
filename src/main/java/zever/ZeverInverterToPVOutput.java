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
		
		for (String s : p.outputs.keySet()) {
			if (postToPVoutput(p.outputs.get(s).toCSV())) {
				p.outputs.remove(s);
			}
		}
		p.save();
	}

	public static boolean postToPVoutput(String inverterData) {
		try {
			Request req = Request.Post(props.getProperty("PVOutput.URL"));
			req.version(HttpVersion.HTTP_1_1)
	        .connectTimeout(1000)
	        .socketTimeout(1000)
			.addHeader("X-Pvoutput-Apikey", props.getProperty("PVOutput.APIKey"))
			.addHeader("X-Pvoutput-SystemId", props.getProperty("PVOutput.SystemId"))
			.bodyForm(Form.form().add("data", inverterData).build());
			HttpResponse response = req.execute().returnResponse();
			logger.info(response.getStatusLine().toString());
			if (response.getStatusLine().getStatusCode() == 200) return true;
		} catch (ClientProtocolException e) {
			logger.error(e.getStackTrace());
		} catch (IOException e) {
			logger.error(e.getStackTrace());
		}
		return false;
	}
}
