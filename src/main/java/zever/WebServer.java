package zever;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

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
	      String response = "1\n" + "1\n" + "EAB96173\n" + "NXVWWXSRRT7\n" + "M11\r\n"
	  			+ "16B21-663R+16B21\n" 
	    		+ "09:28 30/07/2017\n" + // will ignore this and just use current time
				"OK\n" + "1\n" + "SX000660117\n" 
	    		+ "2264\n" + // Assume this is watt hours)
				"2.2\n" + // This needs to become 2.02 apparently
				"OK\n" 
				+ "Error";
	      
	      exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
	      OutputStream os = exchange.getResponseBody();
	      os.write(response.getBytes());
	      os.close();
	  }

}
