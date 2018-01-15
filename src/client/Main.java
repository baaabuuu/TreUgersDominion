package client;


public class Main {
	private static ConnectionHandler connectionHandler;
	public static void main(String[] args) {
		connectionHandler = new ConnectionHandler(8181, "127.0.0.1");
		connectionHandler.run();
	}
}
