package client;

import java.util.Arrays;

public class Parser {
	
	protected static final java.util.List<String> usernames = 
			Arrays.asList("bob", "bill", "jack", "kane","master","fahim");
	protected static final java.util.List<String> passwords = 
			Arrays.asList("passw0rd", "john", "bone", "cage","theKing","brad");
	protected User p1 = new User();
	protected User p2 = new User();
	
	//To be used only by client
	//Takes the string to update all players balances
	//String will look like “P1USERNAME~P1BALANCE;P2USERNAME~P2BALANCE
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
	public void playerBetSet(String input) {
		String[] playerState = input.split(";");
		String[] p1State = playerState[0].split("~");
		String[] p2State = playerState[1].split("~");
		p1.bet = Integer.parseInt(p1State[0]);
		p1.balance = Integer.parseInt(p1State[1]);
		p2.bet = Integer.parseInt(p2State[0]);
		p2.balance = Integer.parseInt(p2State[1]);
	
	}
	
	
	//sent in by server
	//To be used only by client
	//Parses the string for each turn
	public void turnSet(String input) {
		String[] playerState = input.split(";"); //Split into playerturn dealer p1 p2 and chatbox
		String[] dealer = playerState[1].split("~"); //splits the dealer,p1, and p2 into score and cards
		String[] p1State = playerState[2].split("~");
		String[] p2State = playerState[3].split("~");
		User.chatbox.add(playerState[4]);
		User.currentPlayerTurn = playerState[0];
		User.dealersCards.add(dealer[1]);
		User.dealerScore = Integer.parseInt(dealer[0]);
		p1.cards.addLast(p1State[1]);
		p1.score = Integer.parseInt(p1State[0]);
		p2.score = Integer.parseInt(p2State[0]);
		p2.cards.addLast(p2State[1]);
		
	}
	
	//Takes in username and password and checks if its correct
	public boolean authenticate(String input) {
		String[] loginInfo = input.split(";");
		if(usernames.contains(loginInfo[0])) {
			int index = usernames.indexOf(loginInfo[0]);
			if(passwords.get(index).equals(loginInfo[1])) {
				return true;
			}
		}
	    return false;
	}
	
	//taken in by the server and changes state on the server side
	//sent in by the client
	public void actionTaken(String input) {
		User temp = p2;
		String[] action = input.split(";");
		if(p1.username.equals(action[2])) { temp = p1; }
		
		if(action[0].equalsIgnoreCase("h")) {
			temp.cards.add(action[1]);
		}else if(action[0].equalsIgnoreCase("t")) {
			User.chatbox.add(temp.username + ": " + action[1]);
		}else if(action[0].equalsIgnoreCase("d")) {
			temp.bet *=2;
		}
		
	}
	//bascaly a tester function 
	//has format of strings that should be passed in
	public static void main(String args[]) {
		Parser test = new Parser();
		test.playerBetSet("20~3000;15~6000");
		System.out.println(test.p1.balance);
		test.playerSet("jack~800;p2~670");
		System.out.println(test.p1.username);
		test.turnSet("jack;12~S8;10~S3;3~3D;I hate my life");
		System.out.println(User.chatbox.getFirst());
		System.out.println(test.p1.bet);
		
		test.actionTaken("T;i hate everyone here;jack");
		System.out.println(User.chatbox.getLast());
	}
	
}
