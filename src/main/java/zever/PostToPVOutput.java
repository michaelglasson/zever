package zever;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

public class PostToPVOutput {
	
	
	for (String s : p.outputs.keySet()) {
		if (postToPVoutput(p.outputs.get(s).toCSV())) {
			p.outputs.remove(s);
		}
	}
	p.save();


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
