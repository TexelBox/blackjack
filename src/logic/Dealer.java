package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import client.User;
import client.Player.Action;
import server.Controller;

/**
 * Internal logic of black jack dealer. Interfaces with player
 * @author elvinlimpin
 *
 */
public class Dealer {
	private UUID roomID;
	private List<Turn> turns;
	private List<ActionBuffer> actions;
	
	public Dealer(UUID roomID) {
		this.roomID = roomID;
	}
	
	// step 1
	public Turn deal() {
		Turn turn = new Turn();
		this.actions = new ArrayList<ActionBuffer>();
		
		return turn;
	}
	
	// step 2 for each player that calls this
	public ActionBuffer handleAction(String username, Action action) {
		// what should this actually do? Change their hand right?
		
		if(Action.DOUBLE.equals(action)) {
			ActionBuffer pr = new ActionBuffer(username, roomID, action);
			actions.add(pr);
			return pr;
			
		} else if(Action.HIT.equals(action)) {
			ActionBuffer pr = new ActionBuffer(username, roomID, action);
			actions.add(pr);
			return pr;
			
		} else {
			ActionBuffer pr = new ActionBuffer(username, roomID, action);
			actions.add(pr);
			return pr;
			
		}
	}
	
	// step 3
	public Turn computeResults(int turnIndex) {
		Turn turn = turns.get(turnIndex);
		List<Hand> hands = new ArrayList<Hand>();
		List<Integer> balances = new ArrayList<Integer>();
		
		for(ActionBuffer action: actions) {
			// logic
			hands.add(new Hand("")); // fix this
			balances.add(0); // fix this
			
			
		} for(int i = 0; i < actions.size(); i++) {
			turn.addResult(actions.get(i), hands.get(i), (int) balances.get(i));	
		}
		
		turn.setComplete(true);
		this.turns.set(turnIndex, turn);
		
		this.actions = null;
		return turn;
	}

	public UUID getRoomID() {
		return roomID;
	}
}
