package client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import log.Log;

public class Main {
	private static ClientController clientController;
	public static void main(String[] args) {
		String host;
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			Log.important("UnknownHostException");
			host = "localhost";
		}
		clientController = new ClientController(8181, host);
		clientController.run();
	}
}

