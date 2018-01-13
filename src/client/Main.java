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
		MainFrame mainFrame;
		ConnectionHandler connectionHandler;
		
		
		Space clientSpace;
		
		Scanner input = new Scanner(System.in);
		boolean lock = true;
		boolean lock2;
		Object[] obj;
		
		connectionHandler = new ConnectionHandler();
		
		mainFrame = new MainFrame();
		mainFrame.setVisible(true);
		mainFrame.setSize(1280,650);
		
		
		while(lock) {
			lock2 = true;
			
			System.out.print("Write a username: ");
			userName = input.next();
			System.out.print("Type in the IP address of a Server: ");
			host = input.next();
			
			String uri = "tcp://" + host + ":" + port + "/board?conn";
			
			while(lock2) {
				
		        Space hostSpace;
				try {
					clientSpace = new SequentialSpace();
					hostSpace = new RemoteSpace(uri);
					Log.important("Connected to port: " + port);
					
					Thread receiver = new Thread(new Receiver(clientSpace, userName, hostSpace));
					Thread consumer = new Thread(new Consumer(clientSpace, userName, hostSpace));
					consumer.start();
					receiver.start();
					
					try {
						clientSpace.get(new ActualField(userName), new ActualField(ServerCommands.newConnection));
						obj = clientSpace.get(new ActualField(userName), new FormalField(Uri.class));
						consumer.interrupt();
						receiver.interrupt();
						Log.important("Disconnected from port: " + port);
						uri = ((Uri)obj[1]).getUri();
						
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
