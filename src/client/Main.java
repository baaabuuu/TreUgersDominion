package client;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import client.Consumer;
import client.Receiver;

public class Main {
	public static void main(String[] args) {
		String userName;
		String host;
		int port = 8181;
		
		Space clientSpace = new SequentialSpace();
		
		
		Scanner input = new Scanner(System.in);
		
		boolean lock = true;
		
		while(lock) {
			System.out.print("Write a username: ");
			userName = input.next();
			System.out.print("Type in the IP address of a Server: ");
			host = input.next();
			
			
			
			String uri = "tcp://" + host + ":" + port + "/board?conn";
			
	        Space hostSpace;
			try {
				hostSpace = new RemoteSpace(uri);
				
				new Thread(new Receiver(clientSpace, userName, hostSpace)).start();
				new Thread(new Consumer(clientSpace, userName, hostSpace)).start();
				
				lock = false;
			} catch (UnknownHostException e) {
				System.out.println("Host not found");
				//e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
        
		
		
		input.close();
	}
	
	
	
	
	
}
