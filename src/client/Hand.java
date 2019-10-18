package client;

import java.util.ArrayList;
import java.util.List;

import server.Card;

public class Hand {
	private List<Card> cards;

	public Hand(String body) {
		List<Card> cards = new ArrayList<Card>();
		
		String[] msg = body.split("<||>");
		cards.add(new Card(msg[0]));
		cards.add(new Card(msg[1]));
	}
	
	public List<Card> getCards() {
		return new ArrayList<Card>(this.cards);
	}
	
	public int getPoints() {
		return 21;
	}

}
