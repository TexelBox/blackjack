package client;

import java.util.LinkedList;
import java.util.Scanner;
import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;

public class User extends TimerTask {
	
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
			
				if(m.ok()) username = arg1;
				else System.out.println("Invalid user");
			}
			System.out.println("Join the game or spectate?");
			arg1 = scan.nextLine(); // blocks
			service.send(new Message(arg1));

			Timer timer = new Timer();
			timer.schedule(new User(), 0, 100);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void run() { 
		this.updateView();
		if(scan.hasNextLine()) {
			this.service.send(new Message(scan.nextLine()));
		}
	}

	public void updateView() {
		for(Message message: this.service.receive()) {
			System.out.println(message.toString()); // do stuff
		}
	}
}
