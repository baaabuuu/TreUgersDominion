package client;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;

import client.Consumer;
import client.Receiver;
import log.Log;
import objects.*;

public class Main {
	public static void main(String[] args) {
		String userName;
		String host;
		int port = 8181;
		
		Scanner input = new Scanner(System.in);
		boolean lock = true;
		boolean lock2;
		Object[] obj;
		
		while(lock) {
			lock2 = true;
			Space clientSpace = new SequentialSpace();
			System.out.print("Write a username: ");
			userName = input.next();
			System.out.print("Type in the IP address of a Server: ");
			host = input.next();
			
			while(lock2) {
				String uri = "tcp://" + host + ":" + port + "/board?conn";
				
		        Space hostSpace;
				try {
					hostSpace = new RemoteSpace(uri);
					Log.important("Connected to port: " + port);
					
					Thread receiver = new Thread(new Receiver(clientSpace, userName, hostSpace));
					Thread consumer = new Thread(new Consumer(clientSpace, userName, hostSpace));
					consumer.start();
					receiver.start();
					
					try {
						clientSpace.get(new ActualField(userName), new ActualField(ServerCommands.newConnection));
						obj = clientSpace.get(new ActualField(userName), new FormalField(PortNumber.class));
						consumer.interrupt();
						receiver.interrupt();
						Log.important("Disconnected from port: " + port);
						port = ((PortNumber)obj[1]).getPort();
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (UnknownHostException e) {
					System.out.println("Host not found");
					Log.important("Could not connect to port: " + port);
					lock2 = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		input.close();
	}
}
