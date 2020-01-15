package org.glasson.zever;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

public class Collector {
	private static final Properties props = new Properties();

	public static String collect() {
		try (InputStream input = new FileInputStream("properties.txt")) {
			props.load(input);
			Request req = Request.Get(props.getProperty("InverterURL"));
			HttpResponse response = req.execute().returnResponse();
			if (response.getStatusLine().getStatusCode() == 200)
				return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			System.err.println(e.getStackTrace());
		} catch (IOException e) {
			System.err.println(e.getStackTrace());
		}
		return "error fetching data from inverter";
	}
}
