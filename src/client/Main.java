package client;


import java.util.Scanner;

import org.jspace.SequentialSpace;
import org.jspace.Space;

import client.Consumer;
import client.Receiver;
import client.Sender;

public class Main {
	public static String userName;
	
	public static void main(String[] args) {
		
		Space clientSpace = new SequentialSpace();
		
		
		Scanner input = new Scanner(System.in);
		
		System.out.print("Write a username: ");
		userName = input.next();
		input.close();
		
		new Thread(new Receiver(clientSpace)).start();
		new Thread(new Sender(clientSpace)).start();
		new Thread(new Consumer(clientSpace,userName)).start();
		
	}
	
	
	
	
	
}
