package client;


import client.Networking;
import client.Consumer;
import java.util.Scanner;

import org.jspace.SequentialSpace;
import org.jspace.Space;

public class Main {
	public static String userName;
	
	public static void main(String[] args) {
		
		Space gameSpace = new SequentialSpace();
		
		
		Scanner input = new Scanner(System.in);
		
		System.out.print("Write a username: ");
		userName = input.next();
		input.close();
		
		new Thread(new Networking()).start();
		new Thread(new Consumer(gameSpace)).start();
		
		
	}
	
	
	
	public class bState {
		public int[] shopArea;
		public int[] handCount;
		public int[] deckCount;
		public int[] discardCount;
		public int trashCount;
		public int[] vpCount; 
		public bState(int[] a, int[] b, int[] c, int[] d, int e, int[] f) {
			this.shopArea = a;
			this.handCount = b;
			this.deckCount = c;
			this.discardCount = d;
			this.trashCount = e;
			this.vpCount = f;
		}
	}
	
}
