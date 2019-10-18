package client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import client.Message.Verb;
import server.Room;

/**
 * Can instantiate Service to create a "room"
 * Other players can join the same room and start the game
 * 
 * @author elvinlimpin
 *
 */
public class Player {
	
	private String username;
	private Hand hand;
	private int money;
	private Service service;
	private UUID roomID;
	
	public enum Action {
		HIT,
		STAND,
		DOUBLE,
	}

	public static void main(String args[]) {
		/**
		 * 1) Ask for name, set to an identifier
		 * 2a) Ask to create a
		 *     Instantiates new Service()
		 *     Returns a room code
		 *     
		 * 2b) Ask to join a group
		 *     Instantiates a new Service() and finds room by code
		 *     
		 * 3a) Starts a game on a room where this Player is joined
		 * 3b) Terminates game
		 */
		
		Player p = new Player();
	}
	
	public Player() {
		try {
			this.service = new Service();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean authenticate(String username, String password) {
		Message response = service.send(new Message(Verb.AUTHENTICATE, null, username, password));
		
		if(response.ok()) {
			this.username = username;
			this.money = Integer.parseInt(response.getBody());
			return true;
		} else return false;
	}
	
	public void createRoom(String roomName) {
		Message response = service.send(new Message(Verb.CREATE_ROOM, null, this.username, roomName));
		
		if(response.ok()) {
			this.roomID = response.getRoomID();
		} else throw new Error("Can't create a room");
	}
	
	public boolean joinRoom(UUID roomID) {
		Message response = service.send(new Message(Verb.JOIN_ROOM, null, this.username, roomID.toString()));
		
		if(response.ok()) {
			this.roomID = response.getRoomID();
			return true;
		} else {
			// can't join this room
			return false;
		}
	}
	
	public List<Room> getRooms() {
		List<Room> rooms = new ArrayList<Room>();
		Message response = service.send(new Message(Verb.GET_ROOMS, null, this.username));
		
		if(response.ok()) {
			for(int i = 0; i < response.getLength(); i++) {
				Message innerResponse = service.send(new Message(Verb.FETCH_ARRAY, null, this.username, response.getBody()));
				
				if(innerResponse.ok()) {
					Room room = new Room(innerResponse.getBody());
					if(!room.isActive()) {
						rooms.add(room);
					}
				}
			}
			return rooms;
		} else throw new Error("Can't get rooms");	
	}
	
	public void startGame() {
		Message response = service.send(new Message(Verb.START_GAME, this.roomID, this.username));
		
		if(response.ok()) {
			// cool
		}
	}
	
	public void sendMessage(String message) {
		Message response = service.send(new Message(Verb.SEND_MESSAGE, this.roomID, this.username, message));
		
		if(response.ok()) {
			// cool
		}
	}
	
	public void submitAction(Action action) {
		Message response = service.send(new Message(Verb.ACT, this.roomID, this.username, action.toString()));
		
		if(response.ok()) {
			// switch case here
			if(action.equals(Action.HIT)) {
				
			}
		}
	}
	
	public void bet(int amount) {
		this.money -= amount;
		
		Message response = service.send(new Message(Verb.BET, this.roomID, this.username, amount+""));
		
		if(response.ok()) {
			// cool
		}
	}
	
	public void getHand() {
		
		Message response = service.send(new Message(Verb.GET_HAND, this.roomID, this.username));
		
		if(response.ok()) {
			this.hand = new Hand(response.getBody());
		}
	}
	
	public void getResult() {
		
		Message response = service.send(new Message(Verb.GET_RESULT, this.roomID, this.username));
		
		if(response.ok()) {
			this.hand = new Hand(response.getBody());
		}
	}
	
	public int getMoney() {
		return this.money;
	}
}
