package client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;

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
	public int bet = -1; // -1 means blank, will be init after betting window
	public int score = -1; // -1 means blank, will be init after hands delt and update on new cards received
	public static int dealerScore = -1; // -1 means blank, 0 means ?? (hidden)
	public LinkedList<String> cards = new LinkedList<String>();
	public static LinkedList<String> dealersCards = new LinkedList<String>();
	public static LinkedList<String> chatbox = new LinkedList<String>();
	public static String DealerCardChanges;
	public String[] playerChanges = {";"," ","~"," ","~"," ","~"," ","~"," "};
	public static String chatChanges = "";
	Timer timer;
	protected static Scanner scan = null;

	static List<String> outGoing = new ArrayList<String>();

	public static void main(String[] args) {
		API service = null;
		String username = "";
		scan = new Scanner(System.in);

		try {
			service = new API();

			boolean invalidInput = true;
			while(invalidInput) {
				// username...
				System.out.print(View.UI_ENTER_USERNAME);
				String arg1 = scan.nextLine();

				//NOTE: this implicitly error checks for the delimiter ';'
				if (!isValidUsernameStr(arg1)) {
					System.out.print(View.UI_USERNAME_STR_ERROR);
					continue;
				}

				// password...
				System.out.print(View.UI_ENTER_PASSWORD);
				String arg2 = scan.nextLine();

				//NOTE: this implicitly error checks for the delimiter ';'
				if (!isValidPasswordStr(arg2)) {
					System.out.print(View.UI_PASSWORD_STR_ERROR);
					continue;
				}

				// credentials are now valid client side, but still must check if valid on server side...
				// send to server and get validation string back
				Message m = service.sendAndWait(new Message(arg1, arg2));
				if (m.ok()) {
					username = arg1;
					System.out.println("Welcome " + username);
				} else {
					if (m.toString().equals("full")) System.out.print(View.UI_FULL_ERROR); // table is full
					else System.out.print(View.UI_AUTH_ERROR); // username does not exist on server or password is wrong
				}
			}

			// fix balance
			Executors
				.newSingleThreadScheduledExecutor()
				.scheduleAtFixedRate(new User(service, username, 0), 0, 50, TimeUnit.MILLISECONDS);

			// get input
			System.out.print(View.UI_COMMAND_INFO);
			System.out.print(View.UI_ENTER_COMMAND);

			while(scan.hasNextLine()) {
				String nextLine = scan.nextLine();
				//sanitization
				if(nextLine.contains(";")) {
					System.out.print(View.UI_SANTIZATION_ERROR);
				}

				if(nextLine.length() == 0) {

				} else if(nextLine.length() < 3) {
					System.out.print(View.UI_COMMAND_ERROR);
					System.out.print(View.UI_COMMAND_INFO);
				} else switch(nextLine.substring(0,3)) {
					case "/b ":
						try {
							Integer.parseInt(nextLine.substring(4).trim());
						} catch(Exception e) {
							System.out.print(View.UI_BET_ERROR);
							break;
						}
					case "/h ":
					case "/s ":
					case "/d ":
					case "/t ":
					case "/j ":
					case "/q ":
					case "/x ": // testing only
						outGoing.add(nextLine.substring(0,3) + username + ": " + nextLine.substring(3));
						break;

					default:
						System.out.print(View.UI_COMMAND_ERROR);
						System.out.print(View.UI_COMMAND_INFO);

				}
				System.out.print(View.UI_ENTER_COMMAND);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//NOTE: only this constructor should be used when initializing a new player at the table (after join commands processed)
	// all other fields will get updated in their initialization windows (betting, cards delt, game turns, etc.)
	protected User(API service, String username, int balance) {
		this.service = service;
		this.username = username;
		this.balance = balance;
	}


	public int calcScore(User temp) {
		temp.score = 0;
		for(int i = 0;i < temp.cards.size();i++) {
			try {
				// if(temp.cards.get(i).substring(0, 1).equals("1")) {
					// temp.score += 10;
				// }else {
					temp.score = temp.score + Integer.parseInt(temp.cards.get(i).substring(0, 1));
				// }
			//This is in case of a Ace jack king or queen
			}catch(Exception e) {
				if(temp.cards.get(i).substring(0,1).equals("A")) {
					if(temp.score < 11) {
						temp.score += 11;
					}else {
						temp.score += 1;
					}
				}if(temp.cards.get(i).substring(0,1).equals("Q")
						|| temp.cards.get(i).substring(0,1).equals("K")
						|| temp.cards.get(i).substring(0,1).equals("J")
						|| temp.cards.get(i).substring(0,1).equals("T")) {
					temp.score += 10;
				}

			}
		}
		return temp.score;
	}
	// test user
	public User(String username, int balance) {
		this.service = null;
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
		if(this.service!=null) {
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

	// HELPERS...

	//NOTE: "" returns false
	private static boolean isAlphaNumeric(String s) {
		if (null == s) return false;
		if (!s.matches("^[a-zA-Z0-9]+$")) return false;
		
		return true;
	}

	private static boolean isValidUsernameStr(String s) {
		// can't be null
		if (null == s) return false;

		// can't be empty
		if (s.isEmpty()) return false;

		// must be alpha-numeric
		if (!isAlphaNumeric(s)) return false;

		// must be >= 1 (already checked above) and <= max char limit
		if (s.length() > View.NB_USERNAME_CHAR_LIMIT) return false;

		return true;
	}

	private static boolean isValidPasswordStr(String s) {
		// can't be null
		if (null == s) return false;

		// can't be empty
		if (s.isEmpty()) return false;

		// must be alpha-numeric
		if (!isAlphaNumeric(s)) return false;

		// must be >= 1 (already checked above) and <= max char limit
		if (s.length() > View.NB_PASSWORD_CHAR_LIMIT) return false;

		return true;
	}

}
