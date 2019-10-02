package client;

/** 
 * A message object lmao this is so unnecessarily complicated
 * @author elvinlimpin
 *
 */
public class Message {
	private String log;
	private String roomID;
	private String playerID;
	private String gameAction;
	private String serverResponse;
	
	public Message(String protocol) {
		String[] msg = protocol.split("||");
		this.log = msg[0];
		this.roomID = msg[1];
		this.playerID = msg[2];
		this.gameAction = msg[3];
		this.serverResponse = msg[4];
	}

	public Message(String log, String roomID, String playerID, String gameAction) {
		this.log = log;
		this.roomID = roomID;
		this.playerID = playerID;
		this.gameAction = gameAction;
		this.serverResponse = null;
	}

	public String getLog() {
		return log;
	}

	public String getRoomID() {
		return roomID;
	}

	public String getPlayerID() {
		return playerID;
	}

	public String getAction() {
		return gameAction;
	}

	public String getResponse() {
		return serverResponse;
	}
	
	public String toOneString() {
		return this.log + "||" + this.roomID + "||" + this.playerID + "||" + this.gameAction + "||" + this.serverResponse;
	}
}
