package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Card {
	
	public enum Suit {
		HEARTS,
		SPADES,
		DIAMONDS,
		CLUBS
	}
	
	public enum Value {
		ACE,
		TWO,
		THREE,
		FOUR,
		FIVE,
		SIX,
		SEVEN,
		EIGHT,
		NINE,
		TEN,
		JACK,
		QUEEN,
		KING
	}
	
	public Suit suit;
	public Value value;
	
	public Card(Suit suit, Value value) {
		this.suit = suit;
		this.value = value;
	}
	
	public Card(String body) {
		
		String[] msg = body.split("<|>");
		this.suit = Suit.valueOf(msg[0]);
		this.value = Value.valueOf(msg[1]);
	}

	public static List<Card> getShuffledDeck() {
		List<Card> deck = new ArrayList<Card>();
		
		for(Suit suit : Suit.values()) {
			for(Value value: Value.values()) {
				deck.add(new Card(suit, value));
			}
		}
		Collections.shuffle(deck);
		return deck;
	}
	
	public String toString() {
		return suit.toString() + "<|>" + value.toString();
	}
}
