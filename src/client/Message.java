package client;

import java.util.UUID;

/** 
 * A message object lmao this is so unnecessarily complicated
 * @author elvinlimpin
 *
 */
public class Message {
	public enum Verb {
		FETCH_LEADERBOARD,
		FETCH_ROOMS,
		AUTHENTICATE,
		JOIN_ROOM,
		CREATE_ROOM,
		QUIT_GAME,
		
		SPECTATE,
		START_GAME,
		TALK,
		
		ACT,
		BET,
		PROCESS_HAND,
		PROCESS_BALANCE,
		
		FETCH_ARRAY,
		ERROR,
	};
	
	private Verb verb;
	private UUID roomID;
	private String userName;
	private String body;
	private int i;
	private int length;
	
	public Message(String protocol) {
		String[] msg = protocol.split("<|||>");
		this.i = Integer.parseInt(msg[0]);
		this.length = Integer.parseInt(msg[1]);
		this.verb = Verb.valueOf(msg[2]);
		this.roomID = UUID.fromString(msg[3]);
		this.userName = msg[4];
		this.body = msg[5];
	}

	public Message(Verb verb, UUID roomID, String userName) {
		this.verb = verb;
		this.roomID = roomID;
		this.userName = userName;
		this.body = null;
	}	

	public Message(Verb verb, UUID roomID, String userName, String body) {
		this.verb = verb;
		this.roomID = roomID;
		this.userName = userName;
		this.body = body;
	}
	
	public Message(Message m) {
		this.verb = Verb.valueOf(m.getVerbAsString());
		this.roomID = m.getRoomID();
		this.userName = m.getUserName();
		this.body = m.getBody();
	}
	
	public int getIndex() {
		return this.i;
	}
	
	public int getLength() {
		return this.length;
	}

	public String getVerbAsString() {
		return this.verb.toString();
	}
	
	public boolean ok() {
		return !this.verb.equals(Verb.ERROR);
	}

	public UUID getRoomID() {
		return roomID;
	}

	public String getUserName() {
		return userName;
	}

	public String getBody() {
		return body;
	}
	
	public String toOneString() {
		return this.i + "<|||>" + this.length + "<|||>" + this.verb.toString() + "<|||>" + this.roomID + "<|||>" + this.userName + "<|||>" + this.body;
	}
}
