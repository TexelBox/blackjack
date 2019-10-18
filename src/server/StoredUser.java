package server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import client.User;

public class StoredUser extends User {

	private String password;
	
	public StoredUser(String password, User user) {
		super(user);
		try {
			this.password = encrypt(password);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public boolean checkPassword(String password) throws NoSuchAlgorithmException {
		return MessageDigest.isEqual(this.password.getBytes(), this.encrypt(password).getBytes());
	}
	
	public String encrypt(String password) throws NoSuchAlgorithmException {

		MessageDigest messageDigest;
		messageDigest = MessageDigest.getInstance("SHA-256");
	
		messageDigest.update(password.getBytes());
		return new String(messageDigest.digest());
	}
}
