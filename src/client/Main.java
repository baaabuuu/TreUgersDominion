package client;


public class Main {
	private static ClientController clientController;
	public static void main(String[] args) {
		clientController = new ClientController(8181, "127.0.0.1");
		clientController.run();
	}
}

