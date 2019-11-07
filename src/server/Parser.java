package server;

import java.util.List;
import java.util.Arrays;
import client.User;
//import client.User.UserType;

public class Parser {
	protected static final List<String> usernames = Arrays.asList("Aaron", "Amir", "Dom", "Elvin");
	protected static final List<String> passwords = Arrays.asList("1","2","3","4");
	protected static final List<Integer> balances = Arrays.asList(1,9999999,300,400); // min = $1, max = $9999999
	protected List<User> players = Arrays.asList(null, null, null, null); // players at the table
	protected List<User> spectators = Arrays.asList(null, null, null, null, null); // spectators (between rounds this needs to be able to hold everyone connected)

	Parser() { 

	}

	// testing
	public Parser(User p0, User p1, User p2, User p3) {
		this.players = Arrays.asList(p0, p1, p2, p3);
	}

	//TODO: have users become spectators first rather than players like it does here...
	// do this, once timer windows are implemented
	// setPlayer
	public int setUser(String auth) {
		// find an open spot on table if any...
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i) == null) {
				players.set(i, this.getUserInfo(auth.trim().split(":")[0]));
				return i;
			}
			if(i==2) {
				// room is full because we are only supporting 2 players atm
				//TODO: could we set them as spectator here?
				return -1;
			}
		}
		return -1;
	}

	public User getUserInfo(String username) {
		if (null == username) return null;

		int i = usernames.indexOf(username);
		
		// if this username is not saved in list...
		if (-1 == i) return null;

		// construct the user from scratch (hard-coded balance that could be read from file in A4)
		return new User(usernames.get(i), balances.get(i)); //TODO: read balance in from file (A4)
	}

	// getPlayer
	public List<User> getUsers() {
		return players; // bind
	}


	// server API will call this before sending input string to actionTaken()	
	// true will cause API to then send "ok" back to client
	// false will cause API to then send "no" back to client
	// this string should be in the same form that actionTaken uses
	public boolean errorCheck(String input) {
		// currently the only prefixes that get passed in here from API are
		// q, j, b, s, h, d
		// they are also in proper protocol format: "(cmd):(state):(username)"

		String[] parts = input.split(":");
		String cmd = parts[0];
		String state = parts[1];
		String username = parts[2];

		// basically checking if the user by this username can perform this cmd in their current state
		// e.g. we would return false if user is a spectator, but command is a HIT

		//NOTE: this username should be guaranteed to be attached to an online user

		// search in players list first...

		//TODO: change this to be by index loop (for int i...)
		for (User u : players) {
			if (null == u) continue;

			// match...
			if (username.equals(u.username)) {
				switch(cmd) {
					case "b":
						// error 1 (not in betting window)
						//TODO... (use timer state)
						
						// error 2 (in betting window, but already bet)
						//TODO... (use timer state + check if u.bet != -1)

						int betVal = Integer.parseInt(state); // never an exception
						// we know betVal is between 1 and 100

						// error 3 (in betting window, haven't bet yet, but we can't afford this bet)
						if (u.balance < betVal) return false;

						return true;
					case "s":
					case "h":
						// error 1 (not in playerturns window)
						//TODO... (use timer state)

						// error 2 (in playerturns window, but not my turn)
						//TODO... (use timer state + check User.turnID vs. user slot in table -> change for loop to be by index)

						return true;
					case "d":
						// error 1 (not in playerturns window)
						//TODO... (use timer state)

						// error 2 (in playerturns window, but not my turn)
						//TODO... (use timer state + check User.turnID vs. user slot in table -> change for loop to be by index)

						// error 3 (can't double-down when you have more than your first 2 cards)
						if (u.cards.size() > 2) return false;

						// error 4 (can't double-down if you can't afford it)
						if (u.balance < u.bet) return false;
					
						return true;
					default:
						return false; // players can't do any other commands
				}
			}
		}

		// didn't find user in players? must be in spectators...

		for (User u : spectators) {
			if (null == u) continue;

			// match...
			if (username.equals(u.username)) {
				switch(cmd) {
					case "q":
						return true; // spectators can quit (logout) at any time (i think?)
					case "j":
						// error 1 (not in join window)
						//TODO... (use timer state)

						// error 2 (in join window, but table is full)
						//TODO... (use timer state + check if no nulls in players)

						return true;
					default:
						return false; // spectators can't do any other commands
				}
			}
		}

		// if somehow we get here (meaning we didn't find user, then just ignore them)
		return false;
	}

	// Takes in username and password and checks if its correct
	// format: (username):(password)
	public boolean authenticate(String input) {
		String[] loginInfo = input.trim().split(":");
		int index = usernames.indexOf(loginInfo[0]);
		if(-1 != index) {
			return passwords.get(index).equals(loginInfo[1]);
		}
		return false;
	}

	// sent in by the client
	// taken in by the server and changes state here on the server side
	// no error checking in here since all that was already done
	// currently the only prefixes that get passed in here from API are
	// q, j, b, s, h, d
	// they are also in proper protocol format: "(cmd):(state):(username)"
	//TODO: refactor this to use common methods
	public void actionTaken(String input) {

		String[] parts = input.split(":");
		String cmd = parts[0];
		String state = parts[1];
		String username = parts[2];

		switch(cmd) {
			case "q":
				// we know a current spectator issued this...
				// 1. remove them from spectators list
				for (int i = 0; i < spectators.size(); ++i) {
					User u = spectators.get(i);
					if (null == u) continue;
		
					// match...
					if (username.equals(u.username)) {
						spectators.set(i, null);
						//TODO... 2. do we need to close any sockets properly???

						break;
					}
				}
				break;
			case "j":
				// we know a current spectator issued this during the join window and a table spot is open...
				// 1. move user from spectators list to players list
				for (int i = 0; i < spectators.size(); ++i) {
					User u = spectators.get(i);
					if (null == u) continue;
		
					// match...
					if (username.equals(u.username)) {
						for (int j = 0; j < players.size(); ++j) {
							// found first open spot...
							if (null == players.get(j)) {
								players.set(j, new User(u.username, u.balance)); // copy only username and balance over to reset state
								break;
							}
						}
						spectators.set(i, null);

						break;
					}
				}
				break;
			case "b":
				// we know a current player issued this during betting window, haven't bet yet and they can afford this bet (balance >= bet)
				for (User u : players) {
					if (null == u) continue;
		
					// match...
					if (username.equals(u.username)) {
						u.bet = Integer.parseInt(state); // never an exception
						u.balance -= u.bet;
						// safety (special case is when balance has now gone down to 0, so to prevent deadlocks, we give them $1)
						if (u.balance == 0) u.balance = 1;

						break;
					}
				}

				break;
			case "s":
				// we know a current player issued this string duing playerturns window and it's their turn
				for (User u : players) {
					if (null == u) continue;
		
					// match...
					if (username.equals(u.username)) {
						int turnID = Integer.parseInt(User.currentPlayerTurn); // never an exception here
						
						int lastPlayerTurnID = 4; // assume a full table until proven wrong
						for (int i = 0; i < players.size(); ++i) {
							// since players is "sorted" by having nulls at the back, find the first null
							if (null == players.get(i)) {
								lastPlayerTurnID = i;
								break;
							}
						}

						if (turnID == lastPlayerTurnID) turnID = 0; // loop back to dealer
						else ++turnID;

						User.currentPlayerTurn = String.valueOf(turnID);

						break;
					}
				}

				break;
			case "h":
				// we know a current player issued this string duing playerturns window and it's their turn
				for (User u : players) {
					if (null == u) continue;
		
					// match...
					if (username.equals(u.username)) {

						u.cards.add(Card.deckOfCards.pop()); //NOTE: you will never run out of cards with just 4 players and 1 deck
						u.calcScore(u); // updates score

						//TODO: right now, i'm not gonna put the auto-stand in for when a player has reached 21, cause i'd also have to add in logic for if they have 21 on their first 2 cards (could do this later)
						
						// if this hit caused a BUST...
						if (u.score > 21) {
							// this also reacts like they have automatically standed (pass turn along)

							int turnID = Integer.parseInt(User.currentPlayerTurn); // never an exception here
						
							int lastPlayerTurnID = 4; // assume a full table until proven wrong
							for (int i = 0; i < players.size(); ++i) {
								// since players is "sorted" by having nulls at the back, find the first null
								if (null == players.get(i)) {
									lastPlayerTurnID = i;
									break;
								}
							}

							if (turnID == lastPlayerTurnID) turnID = 0; // loop back to dealer
							else ++turnID;

							User.currentPlayerTurn = String.valueOf(turnID);
						} 

						break;
					}
				}

				break;
			case "d":
				// we know a current player issued this string duing playerturns window and it's their turn, they are on their first 2 cards and they can afford the double bet
				for (User u : players) {
					if (null == u) continue;
		
					// match...
					if (username.equals(u.username)) {

						// 1. double the bet

						u.balance -= u.bet;
						u.bet *= 2;
						// safety (special case is when balance has now gone down to 0, so to prevent deadlocks, we give them $1)
						if (u.balance == 0) u.balance = 1;

						// 2. deal another final card and update score

						u.cards.add(Card.deckOfCards.pop()); //NOTE: you will never run out of cards with just 4 players and 1 deck
						u.calcScore(u); // updates score

						// 3. automatically stand (pass turn along)

						int turnID = Integer.parseInt(User.currentPlayerTurn); // never an exception here
					
						int lastPlayerTurnID = 4; // assume a full table until proven wrong
						for (int i = 0; i < players.size(); ++i) {
							// since players is "sorted" by having nulls at the back, find the first null
							if (null == players.get(i)) {
								lastPlayerTurnID = i;
								break;
							}
						}

						if (turnID == lastPlayerTurnID) turnID = 0; // loop back to dealer
						else ++turnID;

						User.currentPlayerTurn = String.valueOf(turnID);
						
						break;
					}
				}

				break;
			default:
				System.out.println("ERROR: Parser.java - invalid action taken (this should never occur)");
		}
		
	}

}
