package server;

import java.util.List;
import java.util.UUID;

import client.Message;
import client.Player;
import client.Spectator;
import logic.Dealer;

import java.util.ArrayList;


public class Room {
	private static final String ACTIVE = "ACTIVE";
	private static final String INACTIVE = "INACTIVE";
	
	protected List<Player> players;
	protected List<Spectator> spectators;
	protected String name;
	private UUID roomID;
	private boolean isActive;
	private Dealer dealer;
	
	public Room(Player player, String name) {
		this.players = new ArrayList<Player>();
		this.name = name;
		this.roomID = UUID.randomUUID();
		this.isActive = false;
		
		this.players.add(player);
	}
	
	public Room(String roomInString) {
		String[] msg = roomInString.split("<||>");
		this.name = msg[0];
		this.roomID = UUID.fromString(msg[1]);
		
		this.players = null;
		this.isActive = msg[3].equals(ACTIVE);
	}
	
	public Room(Room room) {
		this.players = room.getPlayers();
		this.name = room.getName();
		this.roomID = room.getRoomID();
		this.isActive = true;
	}
	
	public List<Player> getPlayers() {
		return new ArrayList<Player>(this.players);
	}
	
	public void addPlayer(Player player) {
		this.players.add(player);
	}
	
	public void addSpectator(Spectator spectator) {
		this.spectators.add(spectator);
	}
	
	
	
	
	public String getName() {
		return this.name;
	}
	
	public UUID getRoomID() {
		return this.roomID;
	}
	
	public Room activate() {
		this.isActive = true;
		this.dealer = new Dealer(this.getRoomID());
		
		return new Room(this);
	}
	
	public String toString() {
		return this.name + "<||>" + this.roomID.toString() + "<||>" + (this.isActive? ACTIVE:INACTIVE);
	}

	public boolean isActive() {
		return isActive;
	}

	public void listen(API service) {
		Message request = service.getMessage();
		
		if(request.getVerbAsString()=="whatever") {
			this.dealer.deal(); // or whatever
		}
		service.respond(new Message("help"));
		
	}
}
