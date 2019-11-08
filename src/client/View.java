package client;

import java.util.Arrays;

import server.Parser;

public class View {

	//TODO: probably should add a UI_RULES string that can be printed to remind everyone of our subset of blackjack rules
	// gonna put these here just to have everything from my mockups...
	// just putting these characters here for safe-keeping (probably can't use them though) - ♥ ♦ ♠ ♣
	public static final String UI_LOGIN_OR_CREATE_ACCOUNT = 
														  "Login or Create Account?\n"
														+ "Login: type '1'\n"
														+ "Create Account: type '2'";
	public static final String UI_LOGIN = "LOGIN";
	public static final String UI_CREATE_ACCOUNT = "CREATE ACCOUNT";
	public static final String UI_ENTER_CHOICE = "Enter choice: ";
	public static final String UI_ENTER_USERNAME = "Enter username: ";
	public static final String UI_ENTER_PASSWORD = "Enter password: ";
	public static final String UI_CONFIRM_PASSWORD = "Confirm password: ";
	public static final String UI_COMMAND_INFO = 
											  "COMMON COMMANDS: /t(alk) <msg>\n"
											+ "PLAYER COMMANDS: /b(et) <value>, /s(tand), /h(it), /d(ouble)\n"
											+ "SPECTATOR COMMANDS: /j(oin), /q(uit)\n";
	public static final String UI_ENTER_COMMAND = "Enter command: ";
	public static final String UI_SANTIZATION_ERROR = "Please do not enter any special characters.\n";
	public static final String UI_BET_ERROR = "Please enter a numeric value after your bet. No decimals.\n";
	public static final String UI_AUTH_ERROR = "Invalid credentials.\n";
	public static final String UI_FULL_ERROR = "No more room for players.\n";
	public static final String UI_COMMAND_ERROR = "\n\nPlease enter one of the following commands:\n";
	public static final String UI_USERNAME_STR_ERROR = "Invalid username string. Must be alpha-numeric and between 1 and 12 characters.\n";
	public static final String UI_PASSWORD_STR_ERROR = "Invalid password string. Must be alpha-numeric and between 1 and 32 characters.\n";


	public static final int NB_CHATBOX_LINES = 24;
	public static final int NB_CHATBOX_LINE_WIDTH = 96;
	public static final int NB_CHATBOX_MSG_CHAR_LIMIT = 82;
	public static final int NB_USERNAME_CHAR_LIMIT = 12;
	public static final int NB_PASSWORD_CHAR_LIMIT = 32;
	public static final int NB_TURN_FIELD_SIZE = 6;
	public static final int NB_SCORE_FIELD_SIZE = 2;
	public static final int NB_BET_FIELD_SIZE = 4;
	public static final int NB_BET_MIN_VALUE = 1;
	public static final int NB_BET_MAX_VALUE = 100; // $100 on /b, but a /d can increase it to $200
	public static final int NB_MAX_CARDS_IN_HAND = 12;
	public static final int NB_BALANCE_FIELD_SIZE = 8;


	// returns the filled in template string for the side-by-side view of game + chat
	// the params are in order that they would appear on table
	public static String getStateUI(Parser parser) {
		User p0 = parser.getUsers().get(0);
		User p1 = parser.getUsers().get(1);
		User p2 = parser.getUsers().get(2);
		User p3 = parser.getUsers().get(3);

		//NOTE: this can be optimized in the future if needed
		//NOTE: i'm assuming that if a user is not null then none of their fields will be null...

		// 1. chatbox...

		// init...
		String[] chatboxLines = new String[NB_CHATBOX_LINES];
		Arrays.fill(chatboxLines, "                                                                                                ");
		// overwrite...
		// assuming the linked list was already capped at a length of 24 and each message string is below tha cap
		for (int i = 0; i < User.chatbox.size(); ++i) {
			chatboxLines[i] = getFixedLengthString(User.chatbox.get(i), NB_CHATBOX_LINE_WIDTH);
		}

		// 2. turns...

		// init...
		String[] turns = new String[5];
		Arrays.fill(turns, "      ");
		// overwrite...
		// assuming exactly 1 turn indicator on the table (turn field will only ever be 0,1,2,3 or 4)
		turns[Integer.parseInt(User.currentPlayerTurn)] = "(TURN)";

		// 2.5 results...
		if (null != p0 && User.Result.NONE != p0.result) turns[1] = getFixedLengthString(p0.result.name(), 6);
		if (null != p1 && User.Result.NONE != p1.result) turns[2] = getFixedLengthString(p1.result.name(), 6);
		if (null != p2 && User.Result.NONE != p2.result) turns[3] = getFixedLengthString(p2.result.name(), 6);
		if (null != p3 && User.Result.NONE != p3.result) turns[4] = getFixedLengthString(p3.result.name(), 6);

		// 3. scores...

		// init...
		String[] scores = new String[5];
		Arrays.fill(scores, "  ");
		// overwrite...
		// assuming a value of -1 means blank and 0 means ?? (hidden, - only applies to dealer's hole card)
		if (-1 != User.dealerScore) scores[0] = (0 == User.dealerScore) ? "??" : getFixedLengthString(String.valueOf(User.dealerScore), NB_SCORE_FIELD_SIZE);
		if (null != p0 && -1 != p0.score) scores[1] = getFixedLengthString(String.valueOf(p0.score), NB_SCORE_FIELD_SIZE);
		if (null != p1 && -1 != p1.score) scores[2] = getFixedLengthString(String.valueOf(p1.score), NB_SCORE_FIELD_SIZE);
		if (null != p2 && -1 != p2.score) scores[3] = getFixedLengthString(String.valueOf(p2.score), NB_SCORE_FIELD_SIZE);
		if (null != p3 && -1 != p3.score) scores[4] = getFixedLengthString(String.valueOf(p3.score), NB_SCORE_FIELD_SIZE);

		// 4. bets...

		// init...
		String[] bets = new String[4];
		Arrays.fill(bets, "    ");
		// overwrite...
		// assuming a value of -1 means blank
		if (null != p0 && -1 != p0.bet) bets[0] = getFixedLengthString("$" + String.valueOf(p0.bet), NB_BET_FIELD_SIZE);
		if (null != p1 && -1 != p1.bet) bets[1] = getFixedLengthString("$" + String.valueOf(p1.bet), NB_BET_FIELD_SIZE);
		if (null != p2 && -1 != p2.bet) bets[2] = getFixedLengthString("$" + String.valueOf(p2.bet), NB_BET_FIELD_SIZE);
		if (null != p3 && -1 != p3.bet) bets[3] = getFixedLengthString("$" + String.valueOf(p3.bet), NB_BET_FIELD_SIZE);

		// 5. cards...

		// init...
		String[][][] cards = new String[5][6][4]; // 5 players (incl. dealer), each has 6x4 rows in card section 
		for (int i = 0; i < cards.length; ++i) {
			for (int j = 0; j < cards[i].length; ++j) {
				Arrays.fill(cards[i][j], "   ");
			}
		}

		// overwrite...
		// dealer's cards...
		// assuming that hidden cards are represented as "??" in the message
		int row = 0;
		int col = 0;
		for (int i = 0; i < User.dealersCards.size(); ++i) {
			cards[0][row][col] = "|" + User.dealersCards.get(i).charAt(0) + "|";
			cards[0][row+1][col] = "|" + User.dealersCards.get(i).charAt(1) + "|";
			
			++col;
			if (col >= 4) {
				row += 2;
				col = 0;
			}
		}

		// P1's cards...
		if (null != p0) {
			row = 0;
			col = 0;
			for (int i = 0; i < p0.cards.size(); ++i) {
				cards[1][row][col] = "|" + p0.cards.get(i).charAt(0) + "|";
				cards[1][row+1][col] = "|" + p0.cards.get(i).charAt(1) + "|";
				
				++col;
				if (col >= 4) {
					row += 2;
					col = 0;
				}
			}
		}

		// P2's cards...
		if (null != p1) {
			row = 0;
			col = 0;
			for (int i = 0; i < p1.cards.size(); ++i) {
				cards[2][row][col] = "|" + p1.cards.get(i).charAt(0) + "|";
				cards[2][row+1][col] = "|" + p1.cards.get(i).charAt(1) + "|";
				
				++col;
				if (col >= 4) {
					row += 2;
					col = 0;
				}
			}
		}

		// P3's cards...
		if (null != p2) {
			row = 0;
			col = 0;
			for (int i = 0; i < p2.cards.size(); ++i) {
				cards[3][row][col] = "|" + p2.cards.get(i).charAt(0) + "|";
				cards[3][row+1][col] = "|" + p2.cards.get(i).charAt(1) + "|";
				
				++col;
				if (col >= 4) {
					row += 2;
					col = 0;
				}
			}
		}

		// P4's cards...
		if (null != p3) {
			row = 0;
			col = 0;
			for (int i = 0; i < p3.cards.size(); ++i) {
				cards[4][row][col] = "|" + p3.cards.get(i).charAt(0) + "|";
				cards[4][row+1][col] = "|" + p3.cards.get(i).charAt(1) + "|";
				
				++col;
				if (col >= 4) {
					row += 2;
					col = 0;
				}
			}
		}

		// 6. usernames...

		// init...
		String[] usernames = new String[4];
		Arrays.fill(usernames, "~VACANT~    "); //NOTE: ~ is used here since this is special character that will never show up in an actual username, so no one can have ~VACANT~ as a username and this never gets passed across the network so its fine
		// overwrite...
		if (null != p0) usernames[0] = getFixedLengthString(p0.username, NB_USERNAME_CHAR_LIMIT);
		if (null != p1) usernames[1] = getFixedLengthString(p1.username, NB_USERNAME_CHAR_LIMIT);
		if (null != p2) usernames[2] = getFixedLengthString(p2.username, NB_USERNAME_CHAR_LIMIT);
		if (null != p3) usernames[3] = getFixedLengthString(p3.username, NB_USERNAME_CHAR_LIMIT);

		// 7. balances...

		// init...
		String[] balances = new String[4];
		Arrays.fill(balances, "        ");
		// overwrite...
		// assuming that the balances have already been clamped between 0 (or 1?) and 9999999
		if (null != p0) balances[0] = getFixedLengthString("$" + String.valueOf(p0.balance), NB_BALANCE_FIELD_SIZE);
		if (null != p1) balances[1] = getFixedLengthString("$" + String.valueOf(p1.balance), NB_BALANCE_FIELD_SIZE);
		if (null != p2) balances[2] = getFixedLengthString("$" + String.valueOf(p2.balance), NB_BALANCE_FIELD_SIZE);
		if (null != p3) balances[3] = getFixedLengthString("$" + String.valueOf(p3.balance), NB_BALANCE_FIELD_SIZE);


		// for now, display the server state here...
		String serverState = getFixedLengthString("STATE="+parser.serverState.name(), 18); //NOTE: if the enum types change names, then this will have to be updated
		serverState = serverState.replaceAll(" ", "=");

		//NOTE: wow, it's ugly...
		String ui =
          "|="+serverState+"===========================================================||;"
        + "|                                    "+turns[0]+"                                    ||;"
        + "|                                    DEALER                                    ||;"
        + "|                                   SCORE:"+scores[0]+"                                   ||;"
        + "|                                 "+cards[0][0][0]+cards[0][0][1]+cards[0][0][2]+cards[0][0][3]+"                                 ||;"
        + "|                                 "+cards[0][1][0]+cards[0][1][1]+cards[0][1][2]+cards[0][1][3]+"                                 ||;"
        + "|                                 "+cards[0][2][0]+cards[0][2][1]+cards[0][2][2]+cards[0][2][3]+"                                 ||;"
        + "|                                 "+cards[0][3][0]+cards[0][3][1]+cards[0][3][2]+cards[0][3][3]+"                                 ||;"
        + "|                                                                              ||;"
        + "|                                                                              ||;"
        + "|                                                                              ||;"
        + "|         "+turns[4]+"            "+turns[3]+"            "+turns[2]+"            "+turns[1]+"         ||;"
        + "|        BET:"+bets[3]+"          BET:"+bets[2]+"          BET:"+bets[1]+"          BET:"+bets[0]+"        ||;"
        + "|                                                                              ||;"
        + "|        SCORE:"+scores[4]+"          SCORE:"+scores[3]+"          SCORE:"+scores[2]+"          SCORE:"+scores[1]+"        ||;"
        + "|      "+cards[4][0][0]+cards[4][0][1]+cards[4][0][2]+cards[4][0][3]+"      "+cards[3][0][0]+cards[3][0][1]+cards[3][0][2]+cards[3][0][3]+"      "+cards[2][0][0]+cards[2][0][1]+cards[2][0][2]+cards[2][0][3]+"      "+cards[1][0][0]+cards[1][0][1]+cards[1][0][2]+cards[1][0][3]+"      ||;"
        + "|      "+cards[4][1][0]+cards[4][1][1]+cards[4][1][2]+cards[4][1][3]+"      "+cards[3][1][0]+cards[3][1][1]+cards[3][1][2]+cards[3][1][3]+"      "+cards[2][1][0]+cards[2][1][1]+cards[2][1][2]+cards[2][1][3]+"      "+cards[1][1][0]+cards[1][1][1]+cards[1][1][2]+cards[1][1][3]+"      ||;"
        + "|      "+cards[4][2][0]+cards[4][2][1]+cards[4][2][2]+cards[4][2][3]+"      "+cards[3][2][0]+cards[3][2][1]+cards[3][2][2]+cards[3][2][3]+"      "+cards[2][2][0]+cards[2][2][1]+cards[2][2][2]+cards[2][2][3]+"      "+cards[1][2][0]+cards[1][2][1]+cards[1][2][2]+cards[1][2][3]+"      ||;"
        + "|      "+cards[4][3][0]+cards[4][3][1]+cards[4][3][2]+cards[4][3][3]+"      "+cards[3][3][0]+cards[3][3][1]+cards[3][3][2]+cards[3][3][3]+"      "+cards[2][3][0]+cards[2][3][1]+cards[2][3][2]+cards[2][3][3]+"      "+cards[1][3][0]+cards[1][3][1]+cards[1][3][2]+cards[1][3][3]+"      ||;"
        + "|                                                                              ||;"
        + "|                                                                              ||;"
        + "|                                                                              ||;"
        + "|      "+usernames[3]+"      "+usernames[2]+"      "+usernames[1]+"      "+usernames[0]+"      ||;"
        + "|      BAL:"+balances[3]+"      BAL:"+balances[2]+"      BAL:"+balances[1]+"      BAL:"+balances[0]+"      ||;"
        + "|                                                                              ||;"
        + "|==============================================================================||";
		
		return ui;
	}
	
	// returns param:original as a string of exact length param:length (left aligned + right padded with whitespace if needed (right truncated))
	public static String getFixedLengthString(String original, int length) {
		if (null == original) return null;

		return String.format("%-"+length+"."+length+"s", original);
	}

	// for testing only...
	public static void main(String[] args) {
		//IN ORDER OF PLAY...
		//printIDLE();
		//printJOIN();
		//printBETSANDHANDSDEALT();
		//printHIT();
		//printTWOMOREHITS();
		//printMSGSANDBUST();
		//printDOUBLEANDROUNDOVER();
		printCLEARTABLE(); // now we are back to IDLE state and the cycle begins again
	}

	// dealer waiting for new round...
	private static void printIDLE() {
		System.out.println(getStateUI(new Parser(null, null, null, null)));
	}
	
	// 3 people have issued join commands...
	private static void printJOIN() {
		// new stuff...
		User p1 = new User("LuckyChucky7", 314);
		User p3 = new User("XGAMBLERx", 1);
		User p4 = new User("JOHN123", 9999999);
		System.out.println(getStateUI(new Parser(p4, p3, null, p1)));
	}

	// all bets are in...
	private static void printBETSANDHANDSDEALT() {
		User p1 = new User("LuckyChucky7", 314);
		User p3 = new User("XGAMBLERx", 1);
		User p4 = new User("JOHN123", 9999999);
		// new stuff...
		// assuming client sides already error checked these numbers and this is the state updates sent back by server
		p1.bet = 42;
		p1.balance -= p1.bet;
		p3.bet = 1;
		//p3.balance -= p3.bet; //NOTE: I don't do this cause they'd get to a balance of 0, which we should clamp to a min of $1 so people don't get locked out of play
		p4.bet = 100;
		p4.balance -= p4.bet;

		// hands should also be distributed now as well (scores were calculated as well)...
		User.dealersCards.add("AH");
		User.dealersCards.add("??");
		User.dealerScore = 0; // means ??
		
		p1.cards.add("AD");
		p1.cards.add("AS");
		p1.score = 12;

		p3.cards.add("KH");
		p3.cards.add("4H");
		p3.score = 14;

		p4.cards.add("AC");
		p4.cards.add("TD");
		p4.score = 21;

		// turn also goes to p1
		User.currentPlayerTurn = "1";

		System.out.println(getStateUI(new Parser(p4, p3, null, p1)));
	}

	// 1 more hit by p1...
	private static void printHIT() {
		User p1 = new User("LuckyChucky7", 314);
		User p3 = new User("XGAMBLERx", 1);
		User p4 = new User("JOHN123", 9999999);
		// assuming client sides already error checked these numbers and this is the state updates sent back by server
		p1.bet = 42;
		p1.balance -= p1.bet;
		p3.bet = 1;
		//p3.balance -= p3.bet; //NOTE: I don't do this cause they'd get to a balance of 0, which we should clamp to a min of $1 so people don't get locked out of play
		p4.bet = 100;
		p4.balance -= p4.bet;

		// hands should also be distributed now as well (scores were calculated as well)...
		User.dealersCards.add("AH");
		User.dealersCards.add("??");
		User.dealerScore = 0; // means ??
		
		p1.cards.add("AD");
		p1.cards.add("AS");
		p1.score = 12;

		p3.cards.add("KH");
		p3.cards.add("4H");
		p3.score = 14;

		p4.cards.add("AC");
		p4.cards.add("TD");
		p4.score = 21;

		// turn also goes to p1
		User.currentPlayerTurn = "1";

		// new stuff...
		p1.cards.add("2D");
		p1.score += 2;

		System.out.println(getStateUI(new Parser(p4, p3, null, p1)));
	}

	// (fast-forward 2 decisions) 2 more hits by p1...
	private static void printTWOMOREHITS() {
		User p1 = new User("LuckyChucky7", 314);
		User p3 = new User("XGAMBLERx", 1);
		User p4 = new User("JOHN123", 9999999);
		// assuming client sides already error checked these numbers and this is the state updates sent back by server
		p1.bet = 42;
		p1.balance -= p1.bet;
		p3.bet = 1; 
		//p3.balance -= p3.bet; //NOTE: I don't do this cause they'd get to a balance of 0, which we should clamp to a min of $1 so people don't get locked out of play
		p4.bet = 100;
		p4.balance -= p4.bet;

		// hands should also be distributed now as well (scores were calculated as well)...
		User.dealersCards.add("AH");
		User.dealersCards.add("??");
		User.dealerScore = 0; // means ??
		
		p1.cards.add("AD");
		p1.cards.add("AS");
		p1.score = 12;

		p3.cards.add("KH");
		p3.cards.add("4H");
		p3.score = 14;

		p4.cards.add("AC");
		p4.cards.add("TD");
		p4.score = 21;

		// turn also goes to p1
		User.currentPlayerTurn = "1";

		p1.cards.add("2D");
		p1.score += 2;

		// new stuff (1)...
		p1.cards.add("4S");
		p1.score += 4;

		// new stuff (2)...
		p1.cards.add("4C");
		p1.score += 4; // this would bust so...
		p1.score -= 10; // now we treat 2nd ace as a value of 1 (11-1 = 10)

		System.out.println(getStateUI(new Parser(p4, p3, null, p1)));
	}

	// 2 chat messages + 1 more hit from p1 that busts them
	private static void printMSGSANDBUST() {
		User p1 = new User("LuckyChucky7", 314);
		User p3 = new User("XGAMBLERx", 1);
		User p4 = new User("JOHN123", 9999999);
		// assuming client sides already error checked these numbers and this is the state updates sent back by server
		p1.bet = 42;
		p1.balance -= p1.bet;
		p3.bet = 1;
		//p3.balance -= p3.bet; //NOTE: I don't do this cause they'd get to a balance of 0, which we should clamp to a min of $1 so people don't get locked out of play
		p4.bet = 100;
		p4.balance -= p4.bet;

		// hands should also be distributed now as well (scores were calculated as well)...
		User.dealersCards.add("AH");
		User.dealersCards.add("??");
		User.dealerScore = 0; // means ??
		
		p1.cards.add("AD");
		p1.cards.add("AS");
		p1.score = 12;

		p3.cards.add("KH");
		p3.cards.add("4H");
		p3.score = 14;

		p4.cards.add("AC");
		p4.cards.add("TD");
		p4.score = 21;

		// turn also goes to p1
		User.currentPlayerTurn = "1";

		p1.cards.add("2D");
		p1.score += 2;

		p1.cards.add("4S");
		p1.score += 4;

		p1.cards.add("4C");
		p1.score += 4; // this would bust so...
		p1.score -= 10; // now we treat 2nd ace as a value of 1 (11-1 = 10)

		// new stuff...
		User.chatbox.add("LuckyChucky7- one more hit?");
		User.chatbox.add("TheBobRoss- why not? remember, there are no mistakes, just happy little accidents!");
		p1.cards.add("JH");
		p1.score += 10; // now we bust for sure :(
		User.currentPlayerTurn = "3"; // bust causes turn to switch to next non-vacant spot

		System.out.println(getStateUI(new Parser(p4, p3, null, p1)));
	}

	// p3 double downs...
	//NOTE: this might get handled differently in the implementation where the client API will have to check if the user actually has a balance >= bet to actually issue a double down
	//NOTE: double-downs can only be performed on the first decision (when you only have 2 cards)
	private static void printDOUBLEANDROUNDOVER() {
		User p1 = new User("LuckyChucky7", 314);
		User p3 = new User("XGAMBLERx", 1);
		User p4 = new User("JOHN123", 9999999);
		// assuming client sides already error checked these numbers and this is the state updates sent back by server
		p1.bet = 42;
		p1.balance -= p1.bet;
		p3.bet = 1;
		//p3.balance -= p3.bet; //NOTE: I don't do this cause they'd get to a balance of 0, which we should clamp to a min of $1 so people don't get locked out of play
		p4.bet = 100;
		p4.balance -= p4.bet;

		// hands should also be distributed now as well (scores were calculated as well)...
		User.dealersCards.add("AH");
		User.dealersCards.add("??");
		User.dealerScore = 0; // means ??
		
		p1.cards.add("AD");
		p1.cards.add("AS");
		p1.score = 12;

		p3.cards.add("KH");
		p3.cards.add("4H");
		p3.score = 14;

		p4.cards.add("AC");
		p4.cards.add("TD");
		p4.score = 21;

		// turn also goes to p1
		User.currentPlayerTurn = "1";

		p1.cards.add("2D");
		p1.score += 2;

		p1.cards.add("4S");
		p1.score += 4;

		p1.cards.add("4C");
		p1.score += 4; // this would bust so...
		p1.score -= 10; // now we treat 2nd ace as a value of 1 (11-1 = 10)

		User.chatbox.add("LuckyChucky7- one more hit?");
		User.chatbox.add("TheBobRoss- why not? remember, there are no mistakes, just happy little accidents!");
		p1.cards.add("JH");
		p1.score += 10; // now we bust for sure :(
		User.currentPlayerTurn = "3"; // bust causes turn to switch to next non-vacant spot

		// new stuff...
		//NOTE: i'm not taking away another bet here since the balance would go to 0 (we could prevent p3 from issuing a double in the first place, but this is for show)
		//p3.balance -= p3.bet; // take away another bet
		p3.bet *= 2; // bet is doubled
		p3.cards.add("3S");
		p3.score += 3;
		User.currentPlayerTurn = "0"; // double-down forces a turn change. Normally it would go to p4 (since its not vacant, but since they have a blackjack, we treat this as an automatic STAND and go directly to the dealer)

		//NOTE: FOR THIS VERSION OF OUR APPLICATION, WE AREN"T ANIMATING THE DEALER'S MOVES SO WE CAN JUST CALCULATE ALL THEIR MOVES IN ONE AND SEND DURING THIS FRAME AS WELL...
		//OUR DEALER RULES: if a dealer gets to 17-21 (soft or hard), they must STAND. Dealer can only bust on a hard hand. e.g. a soft hand of A,2,10 = 13 not a bust of 23. Dealer must hit whenever <= 16
		User.dealersCards.set(1, "5D"); // turn up the hole card (dealer now has soft 16, must hit again)
		User.dealerScore = 16;
		User.dealersCards.add("8H");
		User.dealerScore += 8; // soft 24, so we go down to hard 14, must hit again
		User.dealerScore -= 10; // treat A as 1 now (hard)
		User.dealersCards.add("7S"); // 21 (but not blackjack)
		User.dealerScore += 7;

		// now we evaluate the...
		//WIN CONDITIONS...
		// BJACK, WON, PUSH, LOST, or BUST
		// each player faces dealer
		// if player has BLACKJACK (A+10/J/Q/K) they WIN unless dealer also has BLACKJACK which results in a PUSH
		// BLACKJACK beats out any other 21 point hand
		// if neither player nor dealer busted, highest hand WIN, lowest hand LOSE otherwise PUSH
		// if player busts, they LOSE no matter what dealer does (even BUST themself)
		//NOTE: currently treating a blackjack win as paying the same amount as a standard value win (but, few casinos pay 2-1 for blackjack, but then the odds are better for the player)

		// gonna put a win result message in chatbox
		String winResult = "***RESULTS***- ";
		// p1 busted, so p1 is a BUST (=LOST)
		winResult += p1.username + "-BUST ";
		p1.bet = 0; // clear bet from table

		// p3 lost by value, so p3 is a LOST
		winResult += p3.username + "-LOST ";
		p3.bet = 0; // clear bet from table

		// p4 got a blackjack and dealer got 21, so p4 BLACKJACK (=WON)
		winResult += p4.username + "-BJACK ";
		p4.balance += 2*p4.bet; // get your bet back and house pays you your bet again
		p4.balance = 9999999; // must do this in this case since their balance is too high and we must cap it
		p4.bet = 0; // clear bet from table

		User.chatbox.add(winResult);

		// just testing the worst case scenario (most characters in this string)
		//User.chatbox.add("***RESULTS***- 123456789TET-BJACK 123456789TET-BJACK 123456789TET-BJACK 123456789TET-BJACK ");

		System.out.println(getStateUI(new Parser(p4, p3, null, p1)));
	}

	private static void printCLEARTABLE() {
		User p1 = new User("LuckyChucky7", 314);
		User p3 = new User("XGAMBLERx", 1);
		User p4 = new User("JOHN123", 9999999);
		// assuming client sides already error checked these numbers and this is the state updates sent back by server
		p1.bet = 42;
		p1.balance -= p1.bet;
		p3.bet = 1;
		//p3.balance -= p3.bet; //NOTE: I don't do this cause they'd get to a balance of 0, which we should clamp to a min of $1 so people don't get locked out of play
		p4.bet = 100;
		p4.balance -= p4.bet;

		// hands should also be distributed now as well (scores were calculated as well)...
		User.dealersCards.add("AH");
		User.dealersCards.add("??");
		User.dealerScore = 0; // means ??
		
		p1.cards.add("AD");
		p1.cards.add("AS");
		p1.score = 12;

		p3.cards.add("KH");
		p3.cards.add("4H");
		p3.score = 14;

		p4.cards.add("AC");
		p4.cards.add("TD");
		p4.score = 21;

		// turn also goes to p1
		User.currentPlayerTurn = "1";

		p1.cards.add("2D");
		p1.score += 2;

		p1.cards.add("4S");
		p1.score += 4;

		p1.cards.add("4C");
		p1.score += 4; // this would bust so...
		p1.score -= 10; // now we treat 2nd ace as a value of 1 (11-1 = 10)

		User.chatbox.add("LuckyChucky7- one more hit?");
		User.chatbox.add("TheBobRoss- why not? remember, there are no mistakes, just happy little accidents!");
		p1.cards.add("JH");
		p1.score += 10; // now we bust for sure :(
		User.currentPlayerTurn = "3"; // bust causes turn to switch to next non-vacant spot

		//NOTE: i'm not taking away another bet here since the balance would go to 0 (we could prevent p3 from issuing a double in the first place, but this is for show)
		//p3.balance -= p3.bet; // take away another bet
		p3.bet *= 2; // bet is doubled
		p3.cards.add("3S");
		p3.score += 3;
		User.currentPlayerTurn = "0"; // double-down forces a turn change. Normally it would go to p4 (since its not vacant, but since they have a blackjack, we treat this as an automatic STAND and go directly to the dealer)

		//NOTE: FOR THIS VERSION OF OUR APPLICATION, WE AREN"T ANIMATING THE DEALER'S MOVES SO WE CAN JUST CALCULATE ALL THEIR MOVES IN ONE AND SEND DURING THIS FRAME AS WELL...
		//OUR DEALER RULES: if a dealer gets to 17-21 (soft or hard), they must STAND. Dealer can only bust on a hard hand. e.g. a soft hand of A,2,10 = 13 not a bust of 23. Dealer must hit whenever <= 16
		User.dealersCards.set(1, "5D"); // turn up the hole card (dealer now has soft 16, must hit again)
		User.dealerScore = 16;
		User.dealersCards.add("8H");
		User.dealerScore += 8; // soft 24, so we go down to hard 14, must hit again
		User.dealerScore -= 10; // treat A as 1 now (hard)
		User.dealersCards.add("7S"); // 21 (but not blackjack)
		User.dealerScore += 7;

		// now we evaluate the...
		//WIN CONDITIONS...
		// BJACK, WON, PUSH, LOST, or BUST
		// each player faces dealer
		// if player has BLACKJACK (A+10/J/Q/K) they WIN unless dealer also has BLACKJACK which results in a PUSH
		// BLACKJACK beats out any other 21 point hand
		// if neither player nor dealer busted, highest hand WIN, lowest hand LOSE otherwise PUSH
		// if player busts, they LOSE no matter what dealer does (even BUST themself)
		//NOTE: currently treating a blackjack win as paying the same amount as a standard value win (but, few casinos pay 2-1 for blackjack, but then the odds are better for the player)

		// gonna put a win result message in chatbox
		String winResult = "***RESULTS***- ";
		// p1 busted, so p1 is a BUST (=LOST)
		winResult += p1.username + "-BUST ";
		p1.bet = 0; // clear bet from table

		// p3 lost by value, so p3 is a LOST
		winResult += p3.username + "-LOST ";
		p3.bet = 0; // clear bet from table

		// p4 got a blackjack and dealer got 21, so p4 BLACKJACK (=WON)
		winResult += p4.username + "-BJACK ";
		p4.balance += 2*p4.bet; // get your bet back and house pays you your bet again
		p4.balance = 9999999; // must do this in this case since their balance is too high and we must cap it
		p4.bet = 0; // clear bet from table

		User.chatbox.add(winResult);

		// just testing the worst case scenario (most characters in this string)
		//User.chatbox.add("***RESULTS***- 123456789TET-BJACK 123456789TET-BJACK 123456789TET-BJACK 123456789TET-BJACK ");


		// new stuff...
		p1 = null;
		p3 = null;
		p4 = null;

		User.resetStatics();

		System.out.println(getStateUI(new Parser(null, null, null, null)));
	}

}
