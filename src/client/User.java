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

	public enum Result {
		NONE,
		BJACK,
		WON,
		PUSH,
		LOST,
		BUST
	}

	public Result result = Result.NONE;

	//public enum UserType {
	//	SPECTATOR, // default
	//	PLAYER
	//}

	//public UserType userType = UserType.SPECTATOR; // client can check this type to error check over commands entered before sending to server

	public static String currentPlayerTurn = "0"; // this should never be null and only go from 0-4 (0 means dealer)
	protected API service = null;
	public String username = null; // must be init when new player joins table (NOTE: null will crash UI to inidicate you dun goofed)
	public int balance = -1; // must be init when new player joins table (NOTE: -1 will not crash UI, but a visual balance of -1 will show up to also indicate you dun goofed)
	public int bet = -1; // -1 means blank, will be init after betting window
	public int score = -1; // -1 means blank, will be init after hands delt and update on new cards received
	public static int dealerScore = -1; // -1 means blank, 0 means ?? (hidden)
	public LinkedList<String> cards = new LinkedList<String>();
	public static LinkedList<String> dealersCards = new LinkedList<String>();
	public static LinkedList<String> chatbox = new LinkedList<String>();
	//public static String DealerCardChanges;
	//public String[] playerChanges = {";"," ","~"," ","~"," ","~"," ","~"," "};
	//public static String chatChanges = "";
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
					username = arg1; // save my username on this client
					System.out.println("Welcome " + username);
					invalidInput = false; // break out of loop
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

				// client side error handling below...
				// the client side can do some general error handling here that does not depend on the game state (those errors will be handled on server)
				// in particular, the client can only ever send 1 of the allowed commands
				/*
					1. trim(), check not empty
					2. check char[0] is '/'
					"/q"
					"/j"
					"/b <int val btwn 1 and 100>"
					"/s"
					"/h"
					"/d"
					"/t <currently just make this an alpha-numeric string of size 1 to 82 (right now, no error msg for going over 82, but instead cut the string off - can use View method for this)>"
				*/

				nextLine = nextLine.trim();
				if (nextLine.isEmpty()) {
					//TODO: error msg
					continue; //?
				}

				String[] parts = nextLine.split("\\s+"); // split by whitespace chars
				//NOTE: parts.length cannot be 0, so no need to check
				String cmdStr = parts[0];

				//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~.....remove this
				// ready up the message to send based on protocol if everything is to go right...
				// format: "(cmd):(state):(username)" =:= "(parts[0].charAt(1):parts.length > 1 ? parts[1] : " ":(username)"

				//NOTE: this looks really redundant and I could use fall-through cases here, but i'm leaving it like this so that each case could have different specific error msgs if needed
				switch(cmdStr) {
					//case "/q":
					//	if (1 == parts.length) {
					//		// success, send to server
					//		outGoing.add("q: :" + username);
					//		// if "ok", call system.exit
					//		// else, error msg
					//	} else {
					//		//error
					//	}
					//	break;
					case "/j":
						if (1 == parts.length) {
							// success, send to server
							outGoing.add("j: :" + username);
							// if "ok", do nothing
							// else, error msg
						} else {
							//error
						}
						break;
					case "/b":
						if (2 == parts.length) {
							String betValStr = parts[1];
							if (isNonNegativeInt(betValStr)) {
								//NOTE: an exception should be impossible here
								int betVal = Integer.parseInt(betValStr); // trims any leading zeros
								if (betVal >= View.NB_BET_MIN_VALUE && betVal <= View.NB_BET_MAX_VALUE) {
									// success, send to server
									outGoing.add("b:" + betVal + ":" + username);
									// if "ok", do nothing
									// else, error msg
								} else {
									//error
								}
							} else {
								//error
							}
						} else {
							//error
						}
						break;
					case "/s":
						if (1 == parts.length) {
							// success, send to server
							outGoing.add("s: :" + username);
							// if "ok", do nothing
							// else, error msg
						} else {
							//error
						}
						break;
					case "/h":
						if (1 == parts.length) {
							// success, send to server
							outGoing.add("h: :" + username);
							// if "ok", do nothing
							// else, error msg
						} else {
							//error
						}
						break;
					case "/d":
						if (1 == parts.length) {
							// success, send to server
							outGoing.add("d: :" + username);
							// if "ok", do nothing
							// else, error msg
						} else {
							//error
						}
						break;
					case "/t":
						if (2 <= parts.length) {
							String wordsConcat = "";
							for (int i = 1; i < parts.length; ++i) {
								wordsConcat += parts[i];
							}

							// sanitize...
							// 1. for now, all words must be alpha-numeric (which handles special characters as well)
							if (isAlphaNumeric(wordsConcat)) {

								String msg = nextLine.substring(3).trim();
								// 2. also, if the string is too long, it will get cut off...
								if (msg.length() > View.NB_CHATBOX_MSG_CHAR_LIMIT) msg = msg.substring(0, View.NB_CHATBOX_MSG_CHAR_LIMIT);
								
								// success, send to server
								outGoing.add("t:" + msg + ":" + username);
								// this should always get "ok" back, so do nothing
							} else {
								//error
							}
						} else {
							//error
						}
						break;
					default:
						// error
						System.out.print(View.UI_COMMAND_ERROR);
						System.out.print(View.UI_COMMAND_INFO);
				}
				//outGoing.add(nextLine.substring(0,3) + username + ": " + nextLine.substring(3)); //TODO: delete this after I fix protocol stuff
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


	// this should be fixed, but its untested...
	// for players...
	public int updateScore() {
		int tempScore = 0;
		int acesRemaining = 0; // must add aces last

		for (String card : cards) {
			int value = 0;
			switch(card.charAt(0)) {
				case 'A':
					++acesRemaining;
					break;
				case 'T':
				case 'J':
				case 'Q':
				case 'K':
					value = 10;
					break;
				default:
					// we have 2-9...
					value = Integer.parseInt(card.substring(0, 1)); // no exception
			}
			tempScore += value;
		}

		// handle aces now...
		while (acesRemaining > 0) {
			tempScore += 11; // first treat as soft 11
			// if this causes a bust...
			if (tempScore > 21) tempScore -= 10; // now treat as hard 1 (11-10) 
			--acesRemaining;
		}

		score = tempScore; // update player's score
		return score;
	}


	// for DEALER...
	// return true if hand is hard (all aces are 1 if any)
	// return false if hand is soft (exists an ace as 11)
	public static boolean updateDealerScore() {
		int tempScore = 0;
		int acesRemaining = 0; // must add aces last

		for (String card : dealersCards) {
			int value = 0;
			switch(card.charAt(0)) {
				case 'A':
					++acesRemaining;
					break;
				case 'T':
				case 'J':
				case 'Q':
				case 'K':
					value = 10;
					break;
				default:
					// we have 2-9...
					value = Integer.parseInt(card.substring(0, 1)); // no exception
			}
			tempScore += value;
		}

		int numSoftAces = acesRemaining; // assume all aces will be soft 11
		// handle aces now...
		while (acesRemaining > 0) {
			tempScore += 11; // first treat as soft 11
			// if this causes a bust...
			if (tempScore > 21) {
				tempScore -= 10; // now treat as hard 1 (11-10) 
				--numSoftAces;
			}
			--acesRemaining;
		}

		dealerScore = tempScore; // update dealer's score
		return numSoftAces == 0;
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



			//TODO: get back error states???
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

	//NOTE: "" returns false
	//NOTE: "-1", "+1" returns false
	//NOTE: "0", "00", "01" return true
	private static boolean isNonNegativeInt(String s) {
		if (null == s) return false;
		if (!s.matches("^[0-9]+$")) return false;
		
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
