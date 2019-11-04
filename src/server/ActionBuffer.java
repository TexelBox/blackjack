package server;

import server.Dealer.Action;

public class ActionBuffer {
		String username;
		Action action;

		ActionBuffer(ActionBuffer pr) {
			this.username = pr.getUsername();
			this.action = pr.getAction();
		}
		
		ActionBuffer(String username, Action action) {
			this.username = username;
			this.action = action;
		}

		String getUsername() {
			return this.username;
		}
		
		 Action getAction() {
			return this.action;
		}

	}