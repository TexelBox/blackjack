package client;

/*
 * A simple TCP client that sends messages to a server and display the message
   from the server. 
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 * 
 * Edited by Group 4 CPSC441F2019
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List; 

public class API {
	Socket clientSocket;
	PrintWriter outBuffer;
	BufferedReader inBuffer;

    public API() throws Exception {
        clientSocket = new Socket("10.1.2.29", 9000);

		outBuffer = new PrintWriter(clientSocket.getOutputStream(), true);
        inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));      
    } 
    
    public void send(Message m) {
		outBuffer.println(m.toString());
    }
    
    public List<Message> receive() {
    	String line;
		List<Message> messages = new ArrayList<Message>();
    	try {
			while (inBuffer.ready() && (line = inBuffer.readLine()) != null) {
			   messages.add(new Message(line));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
    	return messages;
	}
	
	public Message sendAndWait(Message m) {
		outBuffer.println("/a " + m.toString());
		try {
			return new Message(inBuffer.readLine());
		} catch (IOException e) {
			e.printStackTrace();
			return new Message();
		}
	}
} 
