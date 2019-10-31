package client; //TODO: move into logic namespace, so that it can be used between server and client APIs

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import client.Message.Verb;
import server.Controller.Authentication;
import server.Room;

public class User {
	
	public enum UserType {
		SPECTATOR, // default
		PLAYER
	}

	public UserType userType = UserType.SPECTATOR; // client can check this type to error check over commands entered before sending to server

	public static String currentPlayerTurn = "0"; // this should never be null and only go from 0-4 (0 means dealer)
	protected API service;
	public String username = null; // must be init when new player joins table (NOTE: null will crash UI to inidicate you dun goofed)
	public int balance = -1; // must be init when new player joins table (NOTE: -1 will not crash UI, but a visual balance of -1 will show up to also indicate you dun goofed)
	protected UUID roomID;
	public int bet = -1; // -1 means blank, will be init after betting window
	public int score = -1; // -1 means blank, will be init after hands delt and update on new cards received
	public static int dealerScore = -1; // -1 means blank, 0 means ?? (hidden)
	public LinkedList<String> cards = new LinkedList<String>();
	public static LinkedList<String> dealersCards = new LinkedList<String>();
	public static LinkedList<String> chatbox = new LinkedList<String>();
	
	public static void main(String args[]) {
		
		User user = new User(); // prompt this
	}

	//NOTE: only this constructor should be used when initializing a new player at the table (after join commands processed)
	// all other fields will get updated in their initialization windows (betting, cards delt, game turns, etc.)
	public User(String username, int balance) {
		this.username = username;
		this.balance = balance;
	}

	public static void resetStatics() {
		currentPlayerTurn = "0";
		dealerScore = -1;
		dealersCards.clear();
		//NOTE: the chatbox does not get cleared here
	}
	
	public User() {
		try {
			this.service = new API();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	public User(User user) {
		this.service = user.service; // do not reinstantiate
		this.username = user.username;
		this.balance = user.getBalance();
		this.roomID = user.roomID;
	}
	

	public User(String body) {
		// TODO Auto-generated constructor stub
	}

	public Authentication authenticate(String username, String password) {
		Message response = service.send(new Message(Verb.AUTHENTICATE, null, username, password));
		
		if(response.ok()) {
			this.username = username;
			return Authentication.OK;
		} else {
			return Authentication.valueOf(response.getBody());
		}
	}
	
	public void fetchBalance() {
		Message response = service.send(new Message(Verb.AUTHENTICATE, null, username));
		
		if(response.ok()) {
			this.balance = Integer.parseInt(response.getBody());
		} else throw new Error("Can't fetch balance");
	}
	
	
	public int getBalance() {
		return this.balance;
	}
	
	public void setBalance(int balance) {
		this.balance = balance;
	}
	

	public void talk(String message) {
		Message response = service.send(new Message(Verb.TALK, this.roomID, this.username, message));
		
		if(response.ok()) {
			// cool
		} else throw new Error("Can't talk");
	}
	
	public List<Room> getJoinableRooms() {
		List<Room> rooms = new ArrayList<Room>();
		Message response = this.service.send(new Message(Verb.FETCH_ROOMS, null, this.username));
		
		if(response.ok()) {
			for(int i = 0; i < response.getLength(); i++) {
				Message innerResponse = this.service.send(new Message(Verb.FETCH_ARRAY, null, this.username, response.getBody()));
				
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
	
	
	public List<Room> getViewableRoom() {
		List<Room> rooms = new ArrayList<Room>();
		Message response = this.service.send(new Message(Verb.FETCH_ROOMS, null, this.username));
		
		if(response.ok()) {
			for(int i = 0; i < response.getLength(); i++) {
				Message innerResponse = this.service.send(new Message(Verb.FETCH_ARRAY, null, this.username, response.getBody()));
				
				if(innerResponse.ok()) {
					rooms.add(new Room(innerResponse.getBody()));
				}
			}
			return rooms;
		} else throw new Error("Can't get rooms");	
	}
	

	
	public void createRoom(String roomName) {
		Message response = this.service.send(new Message(Verb.CREATE_ROOM, null, this.username, roomName));
		
		if(response.ok()) {
			this.roomID = response.getRoomID();
		} else throw new Error("Can't create a room");
	}
	
	public void joinRoom(UUID roomID) {
		Message response = this.service.send(new Message(Verb.JOIN_ROOM, null, this.username, roomID.toString()));
		
		if(response.ok()) {
			this.roomID = response.getRoomID();
		} else throw new Error("Can't join this room");
	}
	
	public Player startGame() {
		Message response = this.service.send(new Message(Verb.START_GAME, this.roomID, this.username));
		
		if(response.ok()) {
			return new Player(this);
		} else throw new Error("Cannot make a new player");
	}
	
	public Spectator spectate() {
		Message response = this.service.send(new Message(Verb.SPECTATE, this.roomID, this.username));
		
		if(response.ok()) {
			return new Spectator(this);
		} else throw new Error("Cannot make a new spectator");
	}
	
	public List<User> fetchLeaderboard() {
		List<User> users = new ArrayList<User>();
		Message response = this.service.send(new Message(Verb.FETCH_LEADERBOARD, null, this.username));
		
		if(response.ok()) {
			for(int i = 0; i < response.getLength(); i++) {
				Message innerResponse = this.service.send(new Message(Verb.FETCH_ARRAY, null, this.username, response.getBody()));
				
				if(innerResponse.ok()) {
					users.add(new User(innerResponse.getBody()));
				}
			}
			return users;
		} else throw new Error("Can't get rooms");
	}
}
