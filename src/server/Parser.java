package server;

import java.util.List;
import java.util.Arrays;
import client.User;
import client.User.UserType;

public class Parser {
	protected static final List<String> usernames = Arrays.asList("Aaron", "Amir", "Dom", "Elvin");
	protected static final List<String> passwords = Arrays.asList("1","2","3","4");
	protected static final List<Integer> balances = Arrays.asList(100,200,300,400);
	protected List<User> users = Arrays.asList(null, null, null, null);
	protected List<User> spectators = Arrays.asList(null, null, null, null, null);

	Parser() { }

	// testing
	public Parser(User p0, User p1, User p2, User p3) {
		this.users = Arrays.asList(p0, p1, p2, p3);
	}

	public int setUser(String auth) {
		for(int i = 0; i < users.size(); i++) {
			if(users.get(i)==null) {
				users.set(i, this.getUserInfo(auth.trim().split(";")[0]));
				return i;
			}
			if(i==2) {
				// room is full because we are only supporting 2 players
				return -1;
			}
		}
		return -1;
	}

	public User getUserInfo(String username) {
		int i = usernames.indexOf(username);
		return new User(usernames.get(i), balances.get(i));
	}

	public List<User> getUsers() {
		return users; // bind
	}

	//To be used only by client
	//Takes the string to update all players balances
	//String will look like *P1USERNAME~P1BALANCE;P2USERNAME~P2BALANCE

	// probably won't be used
	public void playerSet(String input) {
		String[] playerState = input.split(";");
		String[] p1State = playerState[0].split("~");
		String[] p2State = playerState[1].split("~");
		this.users.get(0).username = p1State[0];
		this.users.get(0).balance = Integer.parseInt(p1State[1]);
		this.users.get(1).username = p2State[0];
		this.users.get(1).balance = Integer.parseInt(p2State[1]);
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

		
	public String lineToParserInput(String userInput) {
		return userInput.substring(1).trim().split(":")[0].replace(" ", ";");
	}
    public String betToParserInput(String userInput) {
        return "b;" + userInput.substring(1).trim().split(":")[1].trim() + ";" + userInput.substring(3).replace(":", " ").trim();
    }


	//sent in by server
	//To be used only by client
	//Parses the string for each turn
	public void turnSet(String input) {
		String[] messSeperate = input.split(":");
		String[] playerState = messSeperate[0].split(";"); //Split into playerturn dealer p1 p2 and chatbox
		String[] dealer = playerState[1].split("~"); //splits the dealer,p1, and p2 into score and cards
		String[] p0State = playerState[2].split("~");
		String[] p1State = playerState[3].split("~");



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

		if(!p0State[0].trim().isEmpty()) {
			this.users.get(0).bet = Integer.parseInt(p0State[0]);
		}
		if(!p0State[1].trim().isEmpty()) {
			this.users.get(0).score = Integer.parseInt(p0State[1]);
		}if(!p0State[2].trim().isEmpty()) {
			this.users.get(0).cards.add(p0State[2]);
		}if(!p0State[3].trim().isEmpty()) {
			this.users.get(0).username = p0State[3];
		}if(!p0State[4].trim().isEmpty()) {
			this.users.get(0).balance = Integer.parseInt(p0State[4]);
		}


		//Player 2 turn set
		if(!p1State[0].trim().isEmpty()) {
			this.users.get(1).bet = Integer.parseInt(p1State[0]);
		}
		if(!p1State[1].trim().isEmpty()) {
			this.users.get(1).score = Integer.parseInt(p1State[1]);
		}if(!p1State[2].trim().isEmpty()) {
			this.users.get(1).cards.add(p1State[2]);
		}if(!p1State[3].trim().isEmpty()) {
			this.users.get(1).username = p1State[3];
		}if(!p1State[4].trim().isEmpty()) {
			this.users.get(1).balance = Integer.parseInt(p1State[4]);
		}

		//messages set
		String[] indMessages = messSeperate[1].split(";");
		for(int i = 0;i<indMessages.length;i++) {
			User.chatbox.add(indMessages[i]);
		}


	}

	
	public boolean errorCheck(String input) {
		String[] action = input.split(";");
		int turn = 1;
		User temp = this.users.get(1);
		User other = this.users.get(0);
		
		if(this.users.get(0).username.equals(action[2])) { temp = this.users.get(0);other=this.users.get(1);turn = 0; }
		if(temp.userType.equals(UserType.PLAYER) && User.currentPlayerTurn.equals(String.valueOf(turn))) {
			return true;
		}
		if(!action[0].equalsIgnoreCase("t")) {
			return false;
		 }
		return true;	
	}
	//Amir- hi everyone
	//bob- hi eve

	//Takes in username and password and checks if its correct
	public boolean authenticate(String input) {
		String[] loginInfo = input.trim().split(";");
		if(usernames.contains(loginInfo[0])) {
			int index = usernames.indexOf(loginInfo[0]);
			return passwords.get(index).equals(loginInfo[1]);
		}
		return false;
	}

	//taken in by the server and changes state on the server side
	//sent in by the client
	public void actionTaken(String input) {

		String[] action = input.split(";");
		if(this.users.get(0)==null) {
			if(action[0].equalsIgnoreCase("j")) {
				this.users.get(0).username = action[2];
			}
			return;

		}else if(this.users.get(1)==null) {
			if(action[0].equalsIgnoreCase("j")) {
				this.users.get(1).username = action[2];
			}
			return;
		}else if (action[0].equalsIgnoreCase("j")) {
			return;
		}
		User temp = this.users.get(1);
		User other = this.users.get(0);
		if(this.users.get(0).username.equals(action[2])) { temp = this.users.get(0);other=this.users.get(1); }

		if(action[0].equalsIgnoreCase("h")) {
			temp.cards.add(Card.deckOfCards.pop());
			temp.playerChanges[5] = temp.cards.getLast();
			temp.calcScore(temp);
			temp.playerChanges[3] = String.valueOf(temp.score);
		}else if(action[0].equalsIgnoreCase("t")) {
			User.chatbox.add(temp.username + "- " + action[1]);
			User.chatChanges = User.chatChanges + User.chatbox.getLast();
		}else if(action[0].equalsIgnoreCase("d")) {
			temp.bet *=2;
			temp.cards.add(Card.deckOfCards.pop());
			temp.playerChanges[1] = String.valueOf(temp.bet);
			temp.playerChanges[5] = temp.cards.getLast();
			temp.calcScore(temp);
			temp.playerChanges[3] = String.valueOf(temp.score);

		}else if(action[0].equalsIgnoreCase("b")) {
			temp.bet = Integer.parseInt(action[1]);
			temp.playerChanges[1] = String.valueOf(temp.bet);
		}else if(action[0].equalsIgnoreCase("s")) {
			int next = Integer.parseInt(User.currentPlayerTurn);
			User.currentPlayerTurn = String.valueOf(next++);
		}

	}
	//bascaly a tester function
	//has format of strings that should be passed in
	public static void main(String args[]) {
		Parser test = new Parser(new User("Amir",200),new User("Elvin",20),null,null);
		test.turnSet(" ; ~ ; ~20~AD~ ~ ; ~12~A2~ ~ :I hate my life:hfegfhewjfhjwe");
		System.out.println(test.users.get(0).cards.getFirst());
		System.out.println(test.users.get(0).score);
		test.playerSet("Amir~15;Elvin~13");
		test.actionTaken("d;3;Elvin");
		for(int i = 0;i < test.users.get(0).playerChanges.length;i++) {
			System.out.print(test.users.get(1).playerChanges[i]);
		}
		//; ; ;
//		test.actionTaken("H;i hate everyone here;jack");
//		System.out.println(User.chatbox.getLast());
	}

}
