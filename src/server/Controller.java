package server;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import client.Player;
import client.Spectator;
import client.User;

/**
 * Internal logic of black jack. We can call a library here
 * @author elvinlimpin
 *
 */
public class Controller {
	private static API service;
	private List<StoredUser> userDB;
	private List<Room> rooms;
	
	public enum Authentication {
		OK,
		WRONG_USERNAME,
		WRONG_PASSWORD,
		ERROR,
	}

	public static void main(String args[]) {
		/**
		 * Instantiates the API singleton
		 * 
		 * intercepts all the commands and deals with it
		 */
		System.out.println("IN HERE");
		try {
			System.out.println("IN HERE2");			
			Controller c = new Controller();
			System.out.println("OUT HRE");
		
			// infinite loop through rooms
			service = new API();
			for(Room room: c.rooms) {
				room.listen(service);
			};
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public Controller() {
		this.userDB = null; // fetch from text file or whatever
	}
	
	public Authentication authenticate(String username, String password) {
		StoredUser user = this.findUserbyName(username);
		if(user==null) return Authentication.WRONG_USERNAME;
		try {
			return user.checkPassword(password)
				?	Authentication.OK
				:	Authentication.WRONG_PASSWORD;
		} catch (NoSuchAlgorithmException e) {
			return Authentication.ERROR;
		}
	}
	
	public List<Room> getRooms() {
		return new ArrayList<Room>(this.rooms);
	}
	
	public Room findRoomByID(UUID roomID) {
		for(Room room: rooms) {
			if(room.getRoomID().equals(roomID)) {
				return room;
			}
		}
		return null;
	}
	
	public int findRoomIdx(UUID roomID) {
		for(int i = 0; i < this.rooms.size(); i++) {
			if(rooms.get(i).getRoomID().equals(roomID)) {
				return i;
			}
		}
		return -1;
	}
	
	public StoredUser findUserbyName(String username) {
		for(StoredUser user: userDB) {
			if(user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}
	
	public void updateRoom(Room room) {
		this.rooms.set(this.findRoomIdx(room.getRoomID()), room);
	}
	
	public Room addRoom(Player player, String name) {
		Room room = new Room(player, name);
		this.rooms.add(room);
		
		return room;
	}
	
	public boolean addPlayerToRoom(UUID roomID, String username) {
		Room room = this.findRoomByID(roomID);
		User user = this.findUserbyName(username);
		
		if(room==null||user==null) return false;
		
		room.addPlayer((Player) user);
		this.updateRoom(room);
		return true;
	}
	
	
	public boolean addSpectatorToRoom(UUID roomID, String username) {
		Room room = this.findRoomByID(roomID);
		User user = this.findUserbyName(username);
		
		if(room==null||user==null) return false;
		
		room.addSpectator((Spectator) user);
		this.updateRoom(room);
		return true;
	}
	
	public List<User> getLeaderboard() {
		List<User> sortedUsers = new ArrayList<User>(this.userDB);
		Collections.sort(sortedUsers, new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				StoredUser u1 = (StoredUser) o1;
				StoredUser u2 = (StoredUser) o2;
				
				return u1.getBalance() - u2.getBalance();
			}
		});
		return sortedUsers;
	}
}
