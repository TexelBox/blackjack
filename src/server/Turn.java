package server;

import java.util.ArrayList;
import java.util.List;

public class Turn {
	
	class Result extends ActionBuffer {
		Hand hand;
		int balance;
		
		Result(ActionBuffer ab, Hand hand, int balance) {
			super(ab);
			this.hand = new Hand(hand);
			this.balance = balance;
		}

		public Hand getHand() {
			return new Hand(this.hand);
		}
		
		public int getBalance() {
			return this.balance;
		}
	}
	
	private List<Result> results;
	private int turnNumber;
	private boolean complete;
	
	public Turn() {
		this.results = new ArrayList<Result>();
		this.setComplete(false);
	}

	public int getTurnNumber() {
		return this.turnNumber;
	}
	
	public List<Result> getResults() {
		return new ArrayList<Result>(this.results);
	}
	
	public void addResult(ActionBuffer ab, Hand hand, int balance) {
		results.add(new Result(ab, hand, balance));
	}
	
	public Result findResultByUsername(String username) {
		for(Result result: results) {
			if(result.getUsername().equals(username)) {
				return result;
			}
		}
		return null;
	}
	
	public Hand getHandByUsername(String username) {
		return this.findResultByUsername(username).getHand();
	}
	
	public int getBalanceByUsername(String username) {
		return this.findResultByUsername(username).getBalance();
	}


	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}
}
