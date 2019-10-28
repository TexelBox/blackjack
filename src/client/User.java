package client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import client.Message.Verb;
import server.Controller.Authentication;
import server.Room;

public class User {
	
	
	public static String currentPlayerTurn;
	protected API service;
	protected String username;
	protected int balance;
	protected UUID roomID;
	protected int bet;
	protected int score;
	protected static int dealerScore;
	protected LinkedList<String> cards = new LinkedList<String>();
	protected static LinkedList<String> dealersCards = new LinkedList<String>();
	protected static LinkedList<String> chatbox = new LinkedList<String>();
	
	public static void main(String args[]) {
		
		User user = new User(); // prompt this
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
