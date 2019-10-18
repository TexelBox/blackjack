package client;

import client.Message.Verb;
import logic.Hand;

/**
 * Can instantiate Service to create a "room"
 * Other players can join the same room and start the game
 * 
 * @author elvinlimpin
 *
 */
public class Player extends User {
	
	private Hand hand;
	
	public enum Action {
		HIT,
		STAND,
		DOUBLE,
	}
	
	public Player(User user) {
		super(user);
		this.hand = null;
	}
	
	public void submitAction(Action action) {
		Message response = super.service.send(new Message(Verb.ACT, super.roomID, super.username, action.toString()));
		
		if(response.ok()) {
			
		}
	}
	
	public void bet(int amount) {
		super.setBalance(super.getBalance() - amount);
		
		Message response = super.service.send(new Message(Verb.BET, super.roomID, super.username, amount+""));
		
		if(response.ok()) {
			// cool
		}
	}
	
	public void processHand() {
		Message response = super.service.send(new Message(Verb.PROCESS_HAND, super.roomID, super.username, this.hand.toString()));
		
		if(response.ok()) {
			this.hand = new Hand(response.getBody());
		}
	}
	
	public void processBalance() {
		Message response = super.service.send(new Message(Verb.PROCESS_BALANCE, super.roomID, super.username, super.getBalance()+""));
		
		if(response.ok()) {
			super.setBalance(Integer.parseInt(response.getBody()));
		}
	}
	
	public Spectator Quit() {
		Message response = this.service.send(new Message(Verb.QUIT_GAME, this.roomID, this.username));
		
		if(response.ok()) {
			return new Spectator(this);
		} else throw new Error("Cannot quit lol");
		
	}
}
