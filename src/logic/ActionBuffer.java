package logic;

import java.util.UUID;

import client.Player.Action;

public class ActionBuffer {
		String username;
		Action action;
		UUID roomID;

		ActionBuffer(ActionBuffer pr) {
			this.username = pr.getUsername();
			this.action = pr.getAction();
			this.roomID = pr.getRoomID();
		}
		
		ActionBuffer(String username, UUID roomID, Action action) {
			this.username = username;
			this.action = action;
			this.roomID = roomID;
		}

		UUID getRoomID() {
			return this.roomID;
		}

		String getUsername() {
			return this.username;
		}
		
		 Action getAction() {
			return this.action;
		}

	}