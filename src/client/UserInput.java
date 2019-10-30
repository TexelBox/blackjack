package client;
import java.util.Scanner;
import client.Message.Verb;

class UserInput implements Runnable {
	
	protected User user;
	protected API service;
	
	UserInput(User user, API api) {
		this.user = user;
		this.service = api;
	}

	@Override
	public void run() {
		Boolean lock = true;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Hello world");
		while(lock) {
			Message response = this.service.send(new Message(Verb.ACT, null, null, scanner.nextLine()));
			System.out.println(response.toString());
			
			if(response.toString().equals("UNLOCK"))
				lock = false;
		}
		System.out.println("Bye lol");
		scanner.close();
	}
	
}
