package org.glasson.zever;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;

public class HttpWorker {
	private static Properties props = new Properties();

	public static void setProperties() {
		try (InputStream input = new FileInputStream("properties.txt")) {
			props.load(input);
		} catch (IOException e) {
			System.err.println(e.getStackTrace());
		}
	}

	public static Output collectFromInverter() {
		if (props.isEmpty()) setProperties();
		Request req = Request.Get(props.getProperty("InverterURL"));
		try {
			HttpResponse response = req.execute().returnResponse();
			if (response.getStatusLine().getStatusCode() == 200) {
				return new Output(EntityUtils.toString(response.getEntity()));
			}
		} catch (Exception e) {
			System.err.println(e.getStackTrace());
		}
		return null;
	}

	public static void postOutput(Output o) {
		if (props.isEmpty()) setProperties();
		try {
			Request req = Request.Post(props.getProperty("PVOutput.URL"));
			req.addHeader("X-Pvoutput-Apikey", props.getProperty("PVOutput.APIKey"))
				.addHeader("X-Pvoutput-SystemId", props.getProperty("PVOutput.SystemId"))
				.bodyForm(Form.form()
				.add("data", o.toCSV()).build());
			HttpResponse response = req.execute().returnResponse();
			if (response.getStatusLine().getStatusCode() != 200)
				System.err.println("Error posting daily output to PVOutput. " + response.getStatusLine());
		} catch (Exception e) {
			System.err.println(e.getStackTrace());
		} 
	}
	
	public static void postStatus(Output o) {
		if (props.isEmpty()) setProperties();
		try {
			Request req = Request.Post(props.getProperty("PVOutput.StatusURL"));
			req.addHeader("X-Pvoutput-Apikey", props.getProperty("PVOutput.APIKey"))
				.addHeader("X-Pvoutput-SystemId", props.getProperty("PVOutput.SystemId"))
				.bodyForm(Form.form()
						.add("d", o.date)
						.add("t", o.time)
						.add("v1", o.generated)
						.add("v2", o.power)
						.add("c1", "3")
						.build());
			HttpResponse response = req.execute().returnResponse();
			System.out.println(response.getStatusLine().toString());
			if (response.getStatusLine().getStatusCode() != 200)
				System.err.println("Error posting status to PVOutput. " + response.getStatusLine());
		} catch (Exception e) {
			System.err.println(e.getStackTrace());
		} 
	}
}
