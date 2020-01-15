package org.glasson.zever;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class WebServer {

	public void run() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
		HttpContext context = server.createContext("/home.cgi");
		context.setHandler(WebServer::handleRequest);
		server.start();
	}

	private static void handleRequest(HttpExchange exchange) throws IOException {
		exchange.sendResponseHeaders(200, buildResponse().length);
		OutputStream os = exchange.getResponseBody();
		os.write(buildResponse());
		os.close();
	}

	private static byte[] buildResponse() {
		String start = "1\n2\n3\n4\n5\n6\n";
		DateTimeFormatter zeverDateTime = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
		String dateTime = LocalDateTime.now().format(zeverDateTime) + "\n";
		String middle = "8\n9\n10\n";
		String power = "2000\n";
		String energy = "2.2\n";
		String end = "13\n14";
		return (start + dateTime + middle + power + energy + end).getBytes();
	}

}
