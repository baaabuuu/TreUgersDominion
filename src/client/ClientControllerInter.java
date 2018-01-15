package client;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import clientUI.UIController;
import log.Log;

public interface ClientControllerInter {

	
	public default void run() {	}
	public default void attemptConnection(String newUri) {	}
	public default void newConnection(String newUri) {	}
	
	
	

}
