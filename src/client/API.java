package client;

/*
 * A simple TCP client that sends messages to a server and display the message
   from the server. 
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 * 
 * Edited by Group 4 CPSC441F2019
 */


import java.io.*; 
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption; 

public class API {
    public API() throws Exception {
		/**
		 * Talks to API and instantiates new Room(), returning room
		 * OR
		 * Talks to API and passes room code, returning room
		 * 
		 * Casts to and from Message
		 */

        // Initialize a client socket connection to the server
        Socket clientSocket = new Socket("127.0.0.1", 8080);

        // Initialize input and an output stream for the connection(s)
		PrintWriter outBuffer =
			new PrintWriter(clientSocket.getOutputStream(), true);
		
        BufferedReader inBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 

        // Initialize user input stream
        String line; 
        BufferedReader inFromUser = 
        new BufferedReader(new InputStreamReader(System.in)); 

        // Get user input and send to the server
        // Display the echo message from the server
        System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
        line = inFromUser.readLine();
        while (!line.equals("logout"))
        {
            // Send to the server
			outBuffer.println(line);
            		
            // Getting response from the server
            line = inBuffer.readLine();
            if(line.equals("<<List begin>>")) {
            	line = inBuffer.readLine();
            	while(!line.equals("<<List end>>")) {
            		System.out.println(line);
                	line = inBuffer.readLine();
            	}
            } else {
            	 if(line.equals("<<File begin>>")) {
            		String title = inBuffer.readLine();
             		BufferedWriter bufferedWriter = Files
             				.newBufferedWriter(
             						Paths.get(title), // sets title
             						StandardCharsets.UTF_8,
             						StandardOpenOption.CREATE);
             		
             		try {
             			line = inBuffer.readLine();
             			while(!line.equals("<<File end>>")) {
             				bufferedWriter.write(line + "\n"); // set contents	
             				line = inBuffer.readLine();
             			}
             		} catch(IOException e) {
             			System.out.println("exception");
             		} finally {
             			bufferedWriter.close();
             		}
             			
                 	line = inBuffer.readLine();
             	}
            	System.out.println("Server: " + line);
            }
            System.out.print("Please enter a message to be sent to the server ('logout' to terminate): ");
            line = inFromUser.readLine(); 
        }

        // Close the socket
        clientSocket.close();           
    } 
    
    public Message send(Message m) {
    	/*
    	 * 
    	 * pass this to the back end irl
    	 */
    	
    	return new Message(m);
    }
} 
