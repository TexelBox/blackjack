package client;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;

public class User implements Runnable {
	
	public static String currentPlayerTurn;
	protected API service;
	protected String username;
	protected int balance;
	protected UUID roomID;
	protected int bet;
	protected int score;
	protected static int dealerScore;
	protected LinkedList<String> cards = new LinkedList<String>();
	protected static LinkedList<String> dealersCards = new LinkedList<String>();
	protected static LinkedList<String> chatbox = new LinkedList<String>();
	Timer timer;
	protected static Scanner scan;

	static List<String> outGoing = new ArrayList<String>();

	public static void main(String args[]) {
		API service;
		String username = "";
		String arg1;
		scan = new Scanner(System.in);

		try {
			service = new API();
			
			while(username.equals("")) {
				System.out.println("Username?");
				arg1 = scan.nextLine();
				
				System.out.println("Password?");
				Message m = service.sendAndWait(new Message(arg1, scan.nextLine()));
			
				if(m.ok()) {
					username = arg1;
					System.out.println("Welcome " + username);
				}
				else System.out.println("Invalid user");

			}
			System.out.println("Join the game or spectate?");
			arg1 = scan.nextLine(); // blocks
			service.send(new Message(arg1));

			Executors
				.newSingleThreadScheduledExecutor()
				.scheduleAtFixedRate(new User(service), 0, 100, TimeUnit.MILLISECONDS);
						
			// get input
			System.out.print("input command: ");
			while(scan.hasNextLine()) {
				System.out.print("input command: ");
				outGoing.add(scan.nextLine());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	User(API service) {
		this.service = service;
	}

	// view updates
	public void run() {
		List<Message> messages = this.service.receive();
		for(Message message: messages) {
			System.out.flush();
		}

		for(String message: outGoing) {
			this.service.send(new Message(message));			
		}
		outGoing = new ArrayList<String>();
	}
}
