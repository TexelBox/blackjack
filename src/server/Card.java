package server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
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
	
	
	public static LinkedList<String> deckOfCards = new LinkedList<String>(Arrays.asList(
			"AH","2H","3H","4H","5H","6H","7H","8H","9H","10H","JH","QH","KH",
			"AS","2S","3S","4S","5S","6S","7S","8S","9S","10S","JS","QS","KS", 
			"AD","2D","3D","4D","5D","6D","7D","8D","9D","10D","JD","QD","KD", 
			"AC","2C","3C","4C","5C","6C","7C","8C","9C","10C","JC","QC","KC"));
	public Card(Suit suit, Value value) {
		this.suit = suit;
		this.value = value;
	}
	
	public Card(String body) {
		
		String[] msg = body.split(":");
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
		return suit.toString() + ":" + value.toString();
	}
}
