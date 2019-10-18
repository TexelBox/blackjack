package server;

import java.util.List;

import logic.Turn;

import java.util.ArrayList;

public class Game extends Room {
	private List<Turn> turns;
	
	public Game(Room room) {
		super(room);
		this.turns = new ArrayList<Turn>();
	}
	
	public void pushTurn(Turn t) {
		this.turns.add(t);
	}
}
