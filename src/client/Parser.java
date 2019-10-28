package client;


public class Parser {
	User p1 = new User();
	User p2 = new User();
	
	
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
	
	public void playerBetSet(String input) {
		String[] playerState = input.split(";");
		String[] p1State = playerState[0].split("~");
		String[] p2State = playerState[1].split("~");
		p1.bet = Integer.parseInt(p1State[0]);
		p1.balance = Integer.parseInt(p1State[1]);
		p2.bet = Integer.parseInt(p2State[0]);
		p2.balance = Integer.parseInt(p2State[1]);
	
	}
	//TODO Dealer score and player score idk what they do 
	public void turnSet(String input) {
		String[] playerState = input.split(";"); //Split into playerturn dealer p1 p2 and chatbox
		String[] dealer = playerState[1].split("~"); //splits the dealer,p1, and p2 into score and cards
		String[] p1State = playerState[2].split("~");
		String[] p2State = playerState[3].split("~");
		User.chatbox.add(playerState[4]);
		User.currentPlayerTurn = Integer.parseInt(playerState[0]);
		User.dealersCards.add(dealer[1]);
		p1.cards.addLast(p1State[2]);
		p2.cards.addLast(p2State[2]);
		
	}
	

}
