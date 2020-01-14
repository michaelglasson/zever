package org.glasson.zever;

import java.io.IOException;

public class QuickTest {

	public static void main(String[] args) throws IOException {
		//WebServer server = new WebServer();
		//server.run();
		//System.out.println("09:38".substring("09:38".indexOf(":") + 1));
		//int t = Integer.parseInt("09:38".substring("09:38".indexOf(":") + 1));
		
		int t = Integer.parseInt("09:40".substring("09:40".indexOf(":") + 1));
		t = (t + 4) / 5 * 5;
		String s = String.format("%02d", t);
		System.out.println(s);
		System.out.println("09:40".replaceAll(":\\d\\d", ":" + s));

		
		

	}

}
