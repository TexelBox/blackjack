package client;

import java.util.Arrays;


public class View {

	//TODO: probably should add a UI_RULES string that can be printed to remind everyone of our subset of blackjack rules
	// gonna put these here just to have everything from my mockups...
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
											+ "SPECTATOR COMMANDS: /j(oin), /q(uit)";
	public static final String UI_ENTER_COMMAND = "Enter command: ";

	public static final int NB_CHATBOX_LINES = 24;
	public static final int NB_CHATBOX_LINE_WIDTH = 96;
	public static final int NB_CHATBOX_MSG_CHAR_LIMIT = 82;
	public static final int NB_USERNAME_CHAR_LIMIT = 12;
	public static final int NB_TURN_FIELD_SIZE = 6;
	public static final int NB_SCORE_FIELD_SIZE = 2;
	public static final int NB_BET_FIELD_SIZE = 4;
	public static final int NB_MAX_CARDS_IN_HAND = 12;
	public static final int NB_BALANCE_FIELD_SIZE = 8;


	// returns the filled in template string for the side-by-side view of game + chat
	// the params are in order that they would appear on table
	//NOTE: if we are keeping Player class, then these should be changed to Player
	public static String getStateUI(User p4, User p3, User p2, User p1) {

		String ui = null;

		// for now, don't even use the params, just get something working...
		
		// placeholder...
		//TODO: chatbox messages (we need to decide on size limits for fields that will be used in the UI and elsewhere)




		// these are the finalized strings that get inserted into template (assuming their lengths have already been fixed and they have been properly aligned/padded)...
		// nothing should be null here

		// placeholders values below, but the names will be the same...
		String[] chatboxLines = new String[NB_CHATBOX_LINES];
		Arrays.fill(chatboxLines, "                                                                                                "); //NOTE: i can init the actual arrays like this and then overwrite with linked list entries if they have them

		chatboxLines[0] = getFixedLengthString("LuckyChucky7- one more hit?", NB_CHATBOX_LINE_WIDTH); // example overwrite (later retrive from linked list)
		chatboxLines[1] = getFixedLengthString("TheBobRoss- why not? remember, there are no mistakes, just happy little accidents!", NB_CHATBOX_LINE_WIDTH);

		
		String[] turns = new String[5];
		Arrays.fill(turns, "      ");

		//NOTE: only 1 entry in turns will be non-blank
		turns[1] = "(TURN)"; 


		String[] scores = new String[5];
		Arrays.fill(scores, "  ");

		scores[0] = "??"; // dealer score is index 0
		scores[1] = "18";
		scores[3] = "14";
		scores[4] = "21";


		String[] bets = new String[4];
		Arrays.fill(bets, "    ");

		bets[0] = "$42 ";
		bets[2] = "$1  ";
		bets[3] = "$100";


		String[][][] cards = new String[5][6][4]; // 5 players (incl. dealer), each has 6x4 rows in card section 
		for (int i = 0; i < cards.length; ++i) {
			for (int j = 0; j < cards[i].length; ++j) {
				Arrays.fill(cards[i][j], "   ");
			}
		}

		// just putting these characters here for safe-keeping (probably can't use them though) - ♥ ♦ ♠ ♣

		// dealer's cards...
		cards[0][0][0] = "|A|";
		cards[0][1][0] = "|H|";
		cards[0][0][1] = "|?|";
		cards[0][1][1] = "|?|";

		// P1's cards...
		cards[1][0][0] = "|A|";
		cards[1][1][0] = "|D|";
		cards[1][0][1] = "|A|";
		cards[1][1][1] = "|S|";
		cards[1][0][2] = "|2|";
		cards[1][1][2] = "|D|";
		cards[1][0][3] = "|2|";
		cards[1][1][3] = "|S|";
		cards[1][2][0] = "|2|";
		cards[1][3][0] = "|C|";

		// P3's cards...
		cards[3][0][0] = "|K|";
		cards[3][1][0] = "|H|";
		cards[3][0][1] = "|4|";
		cards[3][1][1] = "|H|";

		// P4's cards...
		cards[4][0][0] = "|A|";
		cards[4][1][0] = "|C|";
		cards[4][0][1] = "|T|";
		cards[4][1][1] = "|D|";


		String[] usernames = new String[4];
		Arrays.fill(usernames, "            ");

		usernames[0] = "LuckyChucky7";
		usernames[2] = "xGAMBLERx   ";
		usernames[3] = "JOHN123     ";


		String[] balances = new String[4];
		Arrays.fill(balances, "        ");

		balances[0] = "$314    ";
		balances[2] = "$1      ";
		balances[3] = "$9999999";


		//NOTE: wow, it's ugly...
		ui =
          "|==============================================================================||================================================================================================|\n"
        + "|                                    "+turns[0]+"                                    ||"+chatboxLines[0]+"|\n"
        + "|                                    DEALER                                    ||"+chatboxLines[1]+"|\n"
        + "|                                   SCORE:"+scores[0]+"                                   ||"+chatboxLines[2]+"|\n"
        + "|                                 "+cards[0][0][0]+cards[0][0][1]+cards[0][0][2]+cards[0][0][3]+"                                 ||"+chatboxLines[3]+"|\n"
        + "|                                 "+cards[0][1][0]+cards[0][1][1]+cards[0][1][2]+cards[0][1][3]+"                                 ||"+chatboxLines[4]+"|\n"
        + "|                                 "+cards[0][2][0]+cards[0][2][1]+cards[0][2][2]+cards[0][2][3]+"                                 ||"+chatboxLines[5]+"|\n"
        + "|                                 "+cards[0][3][0]+cards[0][3][1]+cards[0][3][2]+cards[0][3][3]+"                                 ||"+chatboxLines[6]+"|\n"
        + "|                                                                              ||"+chatboxLines[7]+"|\n"
        + "|                                                                              ||"+chatboxLines[8]+"|\n"
        + "|                                                                              ||"+chatboxLines[9]+"|\n"
        + "|         "+turns[4]+"            "+turns[3]+"            "+turns[2]+"            "+turns[1]+"         ||"+chatboxLines[10]+"|\n"
        + "|        BET:"+bets[3]+"          BET:"+bets[2]+"          BET:"+bets[1]+"          BET:"+bets[0]+"        ||"+chatboxLines[11]+"|\n"
        + "|                                                                              ||"+chatboxLines[12]+"|\n"
        + "|        SCORE:"+scores[4]+"          SCORE:"+scores[3]+"          SCORE:"+scores[2]+"          SCORE:"+scores[1]+"        ||"+chatboxLines[13]+"|\n"
        + "|      "+cards[4][0][0]+cards[4][0][1]+cards[4][0][2]+cards[4][0][3]+"      "+cards[3][0][0]+cards[3][0][1]+cards[3][0][2]+cards[3][0][3]+"      "+cards[2][0][0]+cards[2][0][1]+cards[2][0][2]+cards[2][0][3]+"      "+cards[1][0][0]+cards[1][0][1]+cards[1][0][2]+cards[1][0][3]+"      ||"+chatboxLines[14]+"|\n"
        + "|      "+cards[4][1][0]+cards[4][1][1]+cards[4][1][2]+cards[4][1][3]+"      "+cards[3][1][0]+cards[3][1][1]+cards[3][1][2]+cards[3][1][3]+"      "+cards[2][1][0]+cards[2][1][1]+cards[2][1][2]+cards[2][1][3]+"      "+cards[1][1][0]+cards[1][1][1]+cards[1][1][2]+cards[1][1][3]+"      ||"+chatboxLines[15]+"|\n"
        + "|      "+cards[4][2][0]+cards[4][2][1]+cards[4][2][2]+cards[4][2][3]+"      "+cards[3][2][0]+cards[3][2][1]+cards[3][2][2]+cards[3][2][3]+"      "+cards[2][2][0]+cards[2][2][1]+cards[2][2][2]+cards[2][2][3]+"      "+cards[1][2][0]+cards[1][2][1]+cards[1][2][2]+cards[1][2][3]+"      ||"+chatboxLines[16]+"|\n"
        + "|      "+cards[4][3][0]+cards[4][3][1]+cards[4][3][2]+cards[4][3][3]+"      "+cards[3][3][0]+cards[3][3][1]+cards[3][3][2]+cards[3][3][3]+"      "+cards[2][3][0]+cards[2][3][1]+cards[2][3][2]+cards[2][3][3]+"      "+cards[1][3][0]+cards[1][3][1]+cards[1][3][2]+cards[1][3][3]+"      ||"+chatboxLines[17]+"|\n"
        + "|                                                                              ||"+chatboxLines[18]+"|\n"
        + "|                                                                              ||"+chatboxLines[19]+"|\n"
        + "|                                                                              ||"+chatboxLines[20]+"|\n"
        + "|      "+usernames[3]+"      "+usernames[2]+"      "+usernames[1]+"      "+usernames[0]+"      ||"+chatboxLines[21]+"|\n"
        + "|      BAL:"+balances[3]+"      BAL:"+balances[2]+"      BAL:"+balances[1]+"      BAL:"+balances[0]+"      ||"+chatboxLines[22]+"|\n"
        + "|                                                                              ||"+chatboxLines[23]+"|\n"
        + "|==============================================================================||================================================================================================|";
		
		return ui;
	}
	
	// returns param:original as a string of exact length param:length (left aligned + right padded with whitespace if needed (right truncated))
	public static String getFixedLengthString(String original, int length) {
		if (null == original) return null;

		return String.format("%-"+length+"."+length+"s", original);
	}
	
	

	//UNUSED?...
	public static void log() {
		// chat stuff
	}
	
	public static void action() {
		// deal with action
	}




	// for testing only...
	public static void main(String[] args) {
		System.out.println(getStateUI(null, null, null, null));
	}
}
