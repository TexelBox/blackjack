package server;

/** 
 * A message object lmao this is so unnecessarily complicated
 * @author elvinlimpin
 *
 */
public class Message {
	public enum Verb {
		GET_REQUEST,
		POST_REQUEST,
		GET_RESPONSE,
		POST_RESPONSE,
	};
	
	private Verb verb;
	private int roomID;
	private int playerID;
	private String body;
	
	public Message(String protocol) {
		String[] msg = protocol.split("||");
		this.verb = Verb.valueOf(msg[0]);
		this.roomID = Integer.parseInt(msg[1]);
		this.playerID = Integer.parseInt(msg[2]);
		this.body = msg[3];
	}

	public Message(Verb verb, int roomID, int playerID, String body) {
		this.verb = verb;
		this.roomID = roomID;
		this.playerID = playerID;
		this.body = body;
	}

	public String getVerbAsString() {
		return this.verb.toString();
	}

	public int getRoomID() {
		return roomID;
	}

	public int getPlayerID() {
		return playerID;
	}

	public String getAction() {
		return body;
	}
	
	public String toOneString() {
		return this.verb.toString() + "||" + this.roomID + "||" + this.playerID + "||" + this.body;
	}
}
