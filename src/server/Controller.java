package server;

/**
 * Internal logic of black jack. We can call a library here
 * @author elvinlimpin
 *
 */
public class Controller {
	private static API service;

	public static void main(String args[]) {
		/**
		 * Instantiates the API singleton
		 * 
		 * intercepts all the commands and deals with it
		 */
		try {
			Controller c = new Controller();
		
			// infinite loop through rooms
			service = new API();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	// public List<User> getLeaderboard() {
	// 	List<User> sortedUsers = new ArrayList<User>(this.userDB);
	// 	Collections.sort(sortedUsers, new Comparator<Object>() {

	// 		@Override
	// 		public int compare(Object o1, Object o2) {
	// 			StoredUser u1 = (StoredUser) o1;
	// 			StoredUser u2 = (StoredUser) o2;
				
	// 			return u1.getBalance() - u2.getBalance();
	// 		}
	// 	});
	// 	return sortedUsers;
	// }
}
