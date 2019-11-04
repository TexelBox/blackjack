package client; //TODO: move into logic namespace, so that it can be used between server and client APIs

import java.util.LinkedList;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.List;
import java.util.ArrayList;

public class User implements Runnable {
	
	public enum UserType {
		SPECTATOR, // default
		PLAYER
	}

	public UserType userType = UserType.SPECTATOR; // client can check this type to error check over commands entered before sending to server

	public static String currentPlayerTurn = "0"; // this should never be null and only go from 0-4 (0 means dealer)
	protected API service;
	public String username = null; // must be init when new player joins table (NOTE: null will crash UI to inidicate you dun goofed)
	public int balance = -1; // must be init when new player joins table (NOTE: -1 will not crash UI, but a visual balance of -1 will show up to also indicate you dun goofed)
	protected UUID roomID;
	public int bet = -1; // -1 means blank, will be init after betting window
	public int score = -1; // -1 means blank, will be init after hands delt and update on new cards received
	public static int dealerScore = -1; // -1 means blank, 0 means ?? (hidden)
	public LinkedList<String> cards = new LinkedList<String>();
	public static LinkedList<String> dealersCards = new LinkedList<String>();
	public static LinkedList<String> chatbox = new LinkedList<String>();
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

			// fix balance
			Executors
				.newSingleThreadScheduledExecutor()
				.scheduleAtFixedRate(new User(service, username, 0), 0, 100, TimeUnit.MILLISECONDS);
						
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

	//NOTE: only this constructor should be used when initializing a new player at the table (after join commands processed)
	// all other fields will get updated in their initialization windows (betting, cards delt, game turns, etc.)
	User(API service, String username, int balance) {
		this.service = service;
		this.username = username;
		this.balance = balance;
	}

	public static void resetStatics() {
		currentPlayerTurn = "0";
		dealerScore = -1;
		dealersCards.clear();
		//NOTE: the chatbox does not get cleared here
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
