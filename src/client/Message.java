package client;

/** 
 * A message object lmao this is so unnecessarily complicated
 * @author elvinlimpin
 *
 */

public class Message {
	private String username = "";
	private String password = "";
	private boolean okFlag = false;
	private String message = "";

	public Message(String protocol) {
		switch(protocol) {
			case "ok":
				message = protocol;
				okFlag = true;
				break;
			default:
				// talk
				message = protocol;
		}
	}

	public Message() {
		this.okFlag = false;
	}

	// only for auth
	public Message(String arg1, String arg2) {
		this.username = arg1;
		this.password = arg2;
	}

	public String toString() {
		if(!this.password.equals("")) {
			return this.username + ":" + this.password;
		} else if(!this.message.equals("")) {
			return this.message;
		}

		else return "";
	}

	public boolean ok() {
		return okFlag;
	}
}
