package client;

import java.util.Arrays;

public class Parser {

	protected static final java.util.List<String> usernames = 
			Arrays.asList("bob", "bill", "jack", "kane","master","fahim");
	protected static final java.util.List<String> passwords = 
			Arrays.asList("passw0rd", "john", "bone", "cage","theKing","brad");
	protected User p1 = null;
	protected User p2 = null;

	public void setP1(User user) {
		p1 = user; // bind
	}

	public void setP2(User user) {
		p2 = user; // bind
	}
	
	//To be used only by client
	//Takes the string to update all players balances
	//String will look like *P1USERNAME~P1BALANCE;P2USERNAME~P2BALANCE
	public void playerSet(String input) {
		String[] playerState = input.split(";");
		String[] p1State = playerState[0].split("~");
		String[] p2State = playerState[1].split("~");
		p1.username = p1State[0];
		p1.balance = Integer.parseInt(p1State[1]);
		p2.username = p2State[0];
		p2.balance = Integer.parseInt(p2State[1]);
	}

	//sent in from the server
	//To be used only by client
	//	public void playerBetSet(String input) {
	//		String[] playerState = input.split(";");
	//		String[] p1State = playerState[0].split("~");
	//		String[] p2State = playerState[1].split("~");
	//		p1.bet = Integer.parseInt(p1State[0]);
	//		p1.balance = Integer.parseInt(p1State[1]);
	//		p2.bet = Integer.parseInt(p2State[0]);
	//		p2.balance = Integer.parseInt(p2State[1]);
	//		
	//	}


	//sent in by server
	//To be used only by client
	//Parses the string for each turn
	public void turnSet(String input) {
		String[] messSeperate = input.split(":");
		String[] playerState = messSeperate[0].split(";"); //Split into playerturn dealer p1 p2 and chatbox
		String[] dealer = playerState[1].split("~"); //splits the dealer,p1, and p2 into score and cards
		String[] p1State = playerState[2].split("~");
		String[] p2State = playerState[3].split("~");
		
		
		
		//state set
		if(!playerState[0].trim().isEmpty()) {
			User.currentPlayerTurn = playerState[0];
		}
		
		//dealer turn set
		
		if(!dealer[0].trim().isEmpty()) {
			User.dealerScore = Integer.parseInt(dealer[0]);
		}
		if(!dealer[1].trim().isEmpty()) {
			User.dealersCards.add(dealer[1]);
		}

		
		//player one turn set
		
		if(!p1State[0].trim().isEmpty()) {
			p1.bet = Integer.parseInt(p1State[0]);
		}
		if(!p1State[1].trim().isEmpty()) {
			p1.score = Integer.parseInt(p1State[1]);
		}if(!p1State[2].trim().isEmpty()) {
			p1.cards.add(p1State[2]);
		}if(!p1State[3].trim().isEmpty()) {
			p1.username = p1State[3];
		}if(!p1State[4].trim().isEmpty()) {
			p1.balance = Integer.parseInt(p1State[4]);
		}
		
		
		//Player 2 turn set
		if(!p2State[0].trim().isEmpty()) {
			p2.bet = Integer.parseInt(p2State[0]);
		}
		if(!p2State[1].trim().isEmpty()) {
			p2.score = Integer.parseInt(p2State[1]);
		}if(!p2State[2].trim().isEmpty()) {
			p2.cards.add(p2State[2]);
		}if(!p2State[3].trim().isEmpty()) {
			p2.username = p2State[3];
		}if(!p2State[4].trim().isEmpty()) {
			p2.balance = Integer.parseInt(p2State[4]);
		}
		
		//messages set
		String[] indMessages = messSeperate[1].split(";");
		for(int i = 0;i<indMessages.length;i++) {
			User.chatbox.add(indMessages[i]);
		}


	}

	//Amir- hi everyone
	//bob- hi eve

	//Takes in username and password and checks if its correct
	public int authenticate(String input) {
		String[] loginInfo = input.split(";");
		if(usernames.contains(loginInfo[0])) {
			int index = usernames.indexOf(loginInfo[0]);
			if(passwords.get(index).equals(loginInfo[1])) {
				return 0;
			}
		}
		return 1;
	}

	//taken in by the server and changes state on the server side
	//sent in by the client
	public void actionTaken(String input) {

		String[] action = input.split(";");
		if(p1==null) {
			if(action[0].equalsIgnoreCase("j")) {
				p1.username = action[2];
			}
			return;

		}else if(p2==null) {
			if(action[0].equalsIgnoreCase("j")) {
				p2.username = action[2];
			}
			return;
		}else if (action[0].equalsIgnoreCase("j")) {
			return;
		}
		User temp = p2;
		User other = p1;
		if(p1.username.equals(action[2])) { temp = p1;other=p2; }

		if(action[0].equalsIgnoreCase("h")) {
			//CALCULATE THE CARD VALUE
			temp.cards.add("NEEDS TO BE CALCULATED STILL");
		}else if(action[0].equalsIgnoreCase("t")) {
			User.chatbox.add(temp.username + "- " + action[1]);
		}else if(action[0].equalsIgnoreCase("d")) {
			temp.bet *=2;
			//CALCULATE THE CARD VALUE
			temp.cards.add("NEEDS TO BE CALCULATED STILL"); 
		}else if(action[0].equalsIgnoreCase("b")) {
			temp.bet = Integer.parseInt(action[1]);
		}

	}
	//bascaly a tester function 
	//has format of strings that should be passed in
	public static void main(String args[]) {
		Parser test = new Parser();
		test.turnSet(" ; ~ ; ~20~AD~ ~ ; ~12~A2~ ~ :I hate my life:hfegfhewjfhjwe");
		System.out.println(test.p1.cards.getFirst());
		System.out.println(test.p1.score);
		//; ; ;
//		test.actionTaken("H;i hate everyone here;jack");
//		System.out.println(User.chatbox.getLast());
	}

}
