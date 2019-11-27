package server;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import client.User;
//import client.User.UserType;

public class Parser {

	//TODO: right now state changes are event based, but later on in A4, I want to implement timers to automatically handle this
	// The state will progress in this order and then loop back around
	public enum ServerState {
		JOINING, // start server here, loop back here from dealer turn after ???? (what event to use for now) ????
		BETTING, // get here from joining window, when 2 players have joined table
		PLAYER_TURNS, // get here from betting window, when all players have bet
		DEALER_TURN // get here from player turns window once turnID has now got back to dealer
	}

	protected static final List<String> usernames = Arrays.asList("Aaron", "Amir", "Dom", "Elvin");
	protected static final List<String> passwords = Arrays.asList("1","2","3","4");
	protected static final List<Integer> balances = Arrays.asList(1,9999999,300,400); // min = $1, max = $9999999
	protected List<User> players = Arrays.asList(null, null, null, null); // players at the table
	public List<User> spectators = Arrays.asList(null, null, null, null, null); // spectators (between rounds this needs to be able to hold everyone connected)

	public ServerState serverState = ServerState.JOINING;

	// both times in milliseconds
	private long serverUptimeMillis = 0;
	private long timeLeftOnTimerMillis = 10000;

	private List<String> disconnectedPlayerUsernames = new ArrayList<String>();

	Parser() { 

	}

	// testing
	public Parser(User p0, User p1, User p2, User p3) {
		this.players = Arrays.asList(p0, p1, p2, p3);
	}

	public long getServerUptimeMillis() { return serverUptimeMillis; }
	public long getTimeLeftOnTimerMillis() { return timeLeftOnTimerMillis; }

	public int getNumConnectedUsers() {

		// players and spectators are disjoint ordered sets - 1 connected/authenticated user only ever exists in exactly 1 position in exactly 1 of {players} or {spectators}
		// thus count the number of non-null spots in both sets

		return getNumConnectedPlayers() + getNumConnectedSpectators();
	}

	public int getNumConnectedPlayers() {
		int count = 0;

		for (User p : players) {
			if (null != p) ++count;
		}

		return count;
	}

	public int getNumConnectedSpectators() {
		int count = 0;

		for (User s : spectators) {
			if (null != s) ++count;
		}

		return count;
	}
	

	//NOTE: i'm changing this to now put new connections in as spectators
	// return -1 if server is full (number connected users (non-null spectators+players) == spectators.size())
	// return -1 if server has username already logged in
	public int setUser(String auth) {
		String[] loginInfo = auth.trim().split(":");
		String username = loginInfo[0];

		// check to make sure username is not already logged in...
		for (User p : players) {
			if (null == p) continue;

			if (username.equals(p.username)) return -1; // already logged in!
		}
		for (User s : spectators) {
			if (null == s) continue;

			if (username.equals(s.username)) return -1; // already logged in!
		}

		// get here if not already logged in...

		// check that server is not full...
		// find number of connected users
		int numConnectedUsers = 0;
		for (User p : players) {
			if (null != p) ++numConnectedUsers;
		}
		for (User s : spectators) {
			if (null != s) ++numConnectedUsers;
		}

		if (numConnectedUsers == spectators.size()) return -1; // we full

		// getting here means we can add user to spectators
		for (int i = 0; i < spectators.size(); ++i) {
			if (null == spectators.get(i)) {
				spectators.set(i, getUserInfo(auth.trim().split(":")[0]));
				return i;
			}
		}

		return -1; // this should never happen, but we need it for compile
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

		for (int i = 0; i < players.size(); ++i) {
			User u = players.get(i);

			if (null == u) continue;

			// match...
			if (username.equals(u.username)) {
				switch(cmd) {
					case "b":
						// error 1 (not in betting window)
						if (ServerState.BETTING != serverState) return false;
						
						// error 2 (in betting window, but already bet)
						if (-1 != u.bet) return false;

						int betVal = Integer.parseInt(state); // never an exception
						// we know betVal is between 1 and 100

						// error 3 (in betting window, haven't bet yet, but we can't afford this bet)
						if (u.balance < betVal) return false;

						return true;
					case "s":
					case "h":
						// error 1 (not in playerturns window)
						if (ServerState.PLAYER_TURNS != serverState) return false;

						// error 2 (in playerturns window, but not my turn)
						int currentTurnID = Integer.parseInt(User.currentPlayerTurn); // no exception should occur
						int userTurnID = i+1;
						if (currentTurnID != userTurnID) return false;

						return true;
					case "d":
						// error 1 (not in playerturns window)
						if (ServerState.PLAYER_TURNS != serverState) return false;

						// error 2 (in playerturns window, but not my turn)
						int currentTurnID2 = Integer.parseInt(User.currentPlayerTurn); // no exception should occur
						int userTurnID2 = i+1;
						if (currentTurnID2 != userTurnID2) return false;

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
						if (ServerState.JOINING != serverState) return false;

						// error 2 (in join window, but table is full)
						boolean isFull = true;
						for (User p : players) {
							if (null == p) {
								isFull = false;
								break;
							}
						}
						if (isFull) return false;

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
								spectators.set(i, null);
								break;
							}
						}

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
				// don't need to update anything about this player, so we don't have to locate them
				// just jump to the next turn...
				nextTurn();
				break;
			//TODO: in h/d could just access the user by the currentPlayerTurn index rather than looping to find them 
			case "h":
				// we know a current player issued this string duing playerturns window and it's their turn
				for (User u : players) {
					if (null == u) continue;
		
					// match...
					if (username.equals(u.username)) {

						u.cards.add(Card.deckOfCards.pop()); //NOTE: you will never run out of cards with just 4 players and 1 deck
						u.updateScore(); // updates score

						//TODO: right now, i'm not gonna put the auto-stand in for when a player has reached 21, cause i'd also have to add in logic for if they have 21 on their first 2 cards (could do this later)
						// also, you could try your luck and double-down to hit a T/J/Q/K to still have a 21 (but with worse value that a blackjack 21) and potentially still beat the dealer, but get more money...
						
						// if this hit caused a BUST...
						if (u.score > 21) {
							// this also reacts like they have automatically standed (pass turn along)
							nextTurn();
						} else {
							nextSubTurn(); // didn't bust? well stay on player
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
						u.updateScore(); // updates score

						// 3. automatically stand (pass turn along)
						nextTurn();
						break;
					}
				}
				break;
			default:
				System.out.println("ERROR: Parser.java - invalid action taken (this should never occur)");
		}
	}


	//STATE CHANGERS (TRANSITION EVENTS)...

	//TODO: probably won't be handling client crashes (we would have to make sure their User becomes null and doesn't interrupt the state - e.g. if 1 player has joined, the timer can start ticking down, but then they crash, become null and we should stop or reset this timer)

	private void joiningToBetting() {
		if (ServerState.JOINING != serverState) return;

		// INIT BETTING WINDOW...

		serverState = ServerState.BETTING;
		timeLeftOnTimerMillis = 10000; //TODO: use constant
	}

	private void bettingToPlayerTurns() {
		if (ServerState.BETTING != serverState) return;

		// safety timeout handling (on timeout, players who haven't bet will be given an auto-bet of the minimum $1 since everyone can always bet this amount regardless of their balance)
		for (User p : players) {
			if (null != p && -1 == p.bet) p.bet = 1;
		}

		// INIT PLAYER_TURNS WINDOW...

		// reset deck to full 52 and sorted
		Card.resetDeck();
		// shuffle deck
		Card.shuffleCards();

		// deal cards to everyone...
		//User.dealersCards.clear(); // safety
		User.dealersCards.add(Card.deckOfCards.pop()); // 1 face up card to dealer
		User.dealersCards.add("??"); // 1 symbolic hole card (face-down)
		User.dealerScore = 0; // 0 means ??

		// each player at table gets dealt hand (2 face-up cards)
		for (User p : players) {
			if (null != p) {
				//p.cards.clear(); // safety
				p.cards.add(Card.deckOfCards.pop());
				p.cards.add(Card.deckOfCards.pop());
				p.updateScore();
			} 
		}
		
		// give turn to player 1 (will always be sitting at rightmost spot (spot 1) on table since we add players to table from right to left)
		User.currentPlayerTurn = "1";

		serverState = ServerState.PLAYER_TURNS;
		timeLeftOnTimerMillis = 10000; //TODO: use constant
	}

	// still on the same player
	private void nextSubTurn() {
		timeLeftOnTimerMillis = 10000; //TODO: use constant
	}

	// move to next player over (or dealer)
	private void nextTurn() {
		int turnID = Integer.parseInt(User.currentPlayerTurn); // never an exception here
		
		int lastPlayerTurnID = 4; // assume a full table until proven wrong
		for (int i = 0; i < players.size(); ++i) {
			// since players is "sorted" by having nulls at the back, find the first null
			if (null == players.get(i)) {
				lastPlayerTurnID = i;
				break;
			}
		}

		if (turnID == lastPlayerTurnID) {
			// loop back to dealer
			User.currentPlayerTurn = "0";
			playerTurnsToDealerTurn();
		} else {
			// move to next human player
			++turnID;
			User.currentPlayerTurn = String.valueOf(turnID);
			nextSubTurn();
		}
	}

	//NOTE: this method might be broken up if another state is added
	private void playerTurnsToDealerTurn() {
		if (ServerState.PLAYER_TURNS != serverState) return;

		// dealer follows his rules to get the rest of his hand...
		//NOTE: FOR THIS VERSION OF OUR APPLICATION, WE AREN"T ANIMATING THE DEALER'S MOVES SO WE CAN JUST CALCULATE ALL THEIR MOVES IN ONE AND SEND DURING THIS FRAME AS WELL...
		//OUR DEALER RULES: if a dealer gets to 17-21 (soft or hard), they must STAND. Dealer can only bust on a hard hand. e.g. a soft hand of A,2,10 = 13 not a bust of 23. Dealer must hit whenever <= 16

		// first off, replace the hole (face-down) card with a real card from deck...
		User.dealersCards.set(1, Card.deckOfCards.pop()); // must do this before calculating score since the "??" will throw exception
		boolean isHardHand = User.updateDealerScore(); // change score of ?? to an int

		// 1-16 (soft or hard) is a hit
		// 17-21 (soft or hard) is a stand
		// can only bust on hard hand (forces a stand)
		while(User.dealerScore <= 16) {
			// hit
			User.dealersCards.add(Card.deckOfCards.pop());
			isHardHand = User.updateDealerScore();
		}

		// WIN CONDITIONS + PAYOUTS (since everyone on table now has final cards and final score)...
		// now we evaluate the...
		// WIN CONDITIONS...
		// BJACK = WON, PUSH, LOST = BUST
		// each player faces dealer
		// if player has BLACKJACK (A+T/J/Q/K) they WIN unless dealer also has BLACKJACK which results in a PUSH
		// BLACKJACK beats out any other 21 point hand
		// if neither player nor dealer busted, highest hand WIN, lowest hand LOSE otherwise PUSH
		// if player busts, they LOSE no matter what dealer does (even BUST themself)
		//NOTE: currently treating a blackjack win as paying the same amount as a standard value win (but, few casinos pay 2-1 for blackjack, but then the odds are better for the player)

		// compare each player to dealer, update their result + pay them if (BJACK, WON, PUSH)
		//NOTE: the logic here could probably be simplified, but I believe all cases are covered
		for (User p : players) {
			if (null == p) continue;

			if (p.score > 21) { // player bust
				p.result = User.Result.BUST;
				// no update to balance, they have already lost money since their bet was on the table - round was net loss of bet
			} else if (p.score == 21 && p.cards.size() == 2) { // player BJACK
				if (User.dealerScore == 21 && User.dealersCards.size() == 2) { // dealer BJACK
					p.result = User.Result.PUSH;
					// update balance (player gets their bet back) - round was net gain of 0
					p.balance += p.bet;
					// safety (clamp max balance, just in case)
					if (p.balance > 9999999) p.balance = 9999999;
				} else { // dealer doesn't have blackjack, and thus a player blackjack beats any other hand
					p.result = User.Result.BJACK;
					// update balance (player gets their bet back + dealer pays them their bet value) - round was net gain of bet
					p.balance += 2*p.bet;
					// safety (clamp max balance, just in case)
					if (p.balance > 9999999) p.balance = 9999999;
				}
			} else { // player has normal hand (1-21) w/ 3+ cards
				if (User.dealerScore > 21) { // dealer bust
					p.result = User.Result.WON;
					// update balance (player gets their bet back + dealer pays them their bet value) - round was net gain of bet
					p.balance += 2*p.bet;
					// safety (clamp max balance, just in case)
					if (p.balance > 9999999) p.balance = 9999999;
				} else if (User.dealerScore == 21 && User.dealersCards.size() == 2) { // dealer BJACK
					p.result = User.Result.LOST;
					// no update to balance, they have already lost money since their bet was on the table - round was net loss of bet
				} else { // dealer has normal hand (1-21) w/ 3+ cards
					// highest score wins, otherwise a push (NOTE: number of cards is not a factor)
					if (p.score > User.dealerScore) {
						p.result = User.Result.WON;
						// update balance (player gets their bet back + dealer pays them their bet value) - round was net gain of bet
						p.balance += 2*p.bet;
						// safety (clamp max balance, just in case)
						if (p.balance > 9999999) p.balance = 9999999;
					} else if (p.score == User.dealerScore) {
						p.result = User.Result.PUSH;
						// update balance (player gets their bet back) - round was net gain of 0
						p.balance += p.bet;
						// safety (clamp max balance, just in case)
						if (p.balance > 9999999) p.balance = 9999999;
					} else {
						p.result = User.Result.LOST;
						// no update to balance, they have already lost money since their bet was on the table - round was net loss of bet
					}
				}
			}

			p.bet = -1; // clear bet from table
		}

		serverState = ServerState.DEALER_TURN;
		timeLeftOnTimerMillis = 10000; //TODO: use constant
	}

	private void dealerTurnToJoining() {
		if (ServerState.DEALER_TURN != serverState) return;

		// clear table...
		// move players into spectators list
		for (int i = 0; i < players.size(); ++i) {
			User p = players.get(i);
			if (null == p) continue;

			// first check if player was disconnected...
			if (disconnectedPlayerUsernames.contains(p.username)) {
				players.set(i, null);
				disconnectedPlayerUsernames.remove(p.username);
				continue;
			}

			// find open spot...
			for (int j = 0; j < spectators.size(); ++j) {
				User s = spectators.get(j);
				if (null == s) {
					spectators.set(j, new User(p.username, p.balance));
					players.set(i, null);
					break;
				}
			}
		}

		//TODO: assert that player's list is now all nulls
		//if somehow there were more users on server than spectator list size, then the above wouldn't be able to move all players over and they would be stuck as players (slightly better bug than being wiped from state completely)

		User.resetStatics();

		Card.resetDeck(); // put cards back into deck

		serverState = ServerState.JOINING;
		timeLeftOnTimerMillis = 10000; //TODO: use constant
	}


	// timestep in milliseconds
	public void tickTimer(long deltaTimeMillis) {
		serverUptimeMillis += deltaTimeMillis;
		timeLeftOnTimerMillis -= deltaTimeMillis;

		if (ServerState.JOINING == serverState) {
			// don't tick down timer if we have no players joined yet...
			if (getNumConnectedPlayers() == 0) timeLeftOnTimerMillis = 10000; //TODO: use constant (make sure its the dealer->join time)
		}
		
		// timeout occurs!
		if (timeLeftOnTimerMillis <= 0) {
			timeLeftOnTimerMillis = 0; // clamp

			switch(serverState) {
				case JOINING:
					joiningToBetting();
					break;
				case BETTING:
					bettingToPlayerTurns();
					break;
				case PLAYER_TURNS:
					nextTurn(); //NOTE: the only way this timeout occurs is if player doesn't issue a s/h/d command in their time window (thus, we treat it as if they issued a /s (stand))
					break;
				case DEALER_TURN:
					dealerTurnToJoining();
					break;
				default: // impossible

			}
		}
	}


	public void handleDisconnect(String username) {
		if (null == username) return;

		// search players first...
		for (User p : players) {
			if (null == p) continue;

			if (username.equals(p.username)) {
				// delay logout of player until round is over (auto-timer events will handle their actions)
				disconnectedPlayerUsernames.add(username);
				return;
			}
		}

		// search spectators next...
		for (User s : spectators) {
			if (null == s) continue;

			if (username.equals(s.username)) {
				// logout spectator immedietely...
				logoutSpectator(username);
				return;
			}
		}
	}


	private void logoutSpectator(String username) {
		if (null == username) return;

		// search...
		for (int i = 0; i < spectators.size(); ++i) {
			User s = spectators.get(i);
			if (null == s) continue;

			// match...
			if (username.equals(s.username)) {
				spectators.set(i, null);
				break;
			}
		}
	}

}
