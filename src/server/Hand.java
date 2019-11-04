package logic;

import java.util.ArrayList;
import java.util.List;

public class Hand {
	private List<Card> cards;

	public Hand(String body) {
		List<Card> cards = new ArrayList<Card>();
		
		String[] msg = body.split("<||>");
		cards.add(new Card(msg[0]));
		cards.add(new Card(msg[1]));
	}
	
	public Hand(Hand hand) {
		this.cards = hand.getCards();
	}

	public List<Card> getCards() {
		return new ArrayList<Card>(this.cards);
	}
	
	// game logic here
	public int getPoints() {
		return 21;
	}
	
	public String toString() {
		String accumulator = "";
		for(Card card: this.cards) {
			accumulator += card.toString() + "<||>";
		}
		return accumulator;
	}

}
