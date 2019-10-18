package server;


import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

import client.Message;

/*
 * A simple TCP select server that accepts multiple connections and echo message back to the clients
 * For use in CPSC 441 lectures
 * Instructor: Prof. Mea Wang
 * 
 * Edited by Group 4 CPSC441F2019
 * 
 * Interfaces between the client Service and the backend
 * 
 * Casts to and from Message
 */

public class API {
    public static int BUFFERSIZE = 32;
    public static int TIMEOUT_LENGTH = 200;
    
    public API() throws Exception {

        // Initialize buffers and coders for channel receive and send
        String line = "";
        Charset charset = Charset.forName( "us-ascii" );  
        CharsetDecoder decoder = charset.newDecoder();  
        CharsetEncoder encoder = charset.newEncoder();
        ByteBuffer inBuffer = null;
        CharBuffer cBuffer = null;
        int bytesSent, bytesRecv;     // number of bytes sent or received
        
        int portNum = 8080;
        
        // Initialize the selector
        Selector selector = Selector.open();

        // Create a server channel and make it non-blocking
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
       
        // Get the port number and bind the socket
        InetSocketAddress isa = new InetSocketAddress(portNum);
        channel.socket().bind(isa);

        // Register that the server selector is interested in connection requests
        channel.register(selector, SelectionKey.OP_ACCEPT);

        // Wait for something happen among all registered sockets
        try {
            boolean terminated = false;
            while (!terminated) {

                if (selector.select(TIMEOUT_LENGTH) < 0) {
                    System.out.println("select() failed");
                    System.exit(1);
                }
                    
                // Get set of ready sockets
                Set readyKeys = selector.selectedKeys();
                Iterator readyItor = readyKeys.iterator();
                // Walk through the ready set
                while (readyItor.hasNext()) {
                    // Get key from set
                    SelectionKey key = (SelectionKey)readyItor.next();

                    // Remove current entry
                    readyItor.remove();

                    // Accept new connections, if any
                    if (key.isAcceptable()) {
                        
                        SocketChannel cchannel = ((ServerSocketChannel)key.channel()).accept();
                        cchannel.configureBlocking(false);
                        System.out.println("Accept conncection from " + cchannel.socket().toString());
                        
                        // Register the new connection for read operation
                        cchannel.register(selector, SelectionKey.OP_READ);
                    } else {
                        SocketChannel cchannel = (SocketChannel)key.channel();
                        if (key.isReadable()){
                            Socket socket = cchannel.socket();
                        
                            // Open input and output streams
                            inBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
                            cBuffer = CharBuffer.allocate(BUFFERSIZE);
                             
                            // Read from socket
                            bytesRecv = cchannel.read(inBuffer);
                            if (bytesRecv <= 0)
                            {
                                System.out.println("read() error, or connection closed");
                                key.cancel();  // deregister the socket
                                continue;
                            }
                             
                            inBuffer.flip();      // make buffer available  
                            decoder.decode(inBuffer, cBuffer, false);
                            cBuffer.flip();
                            line = cBuffer.toString();
                            System.out.print("TCP Client: " + line);
                   

                			File dir = new File(System.getProperty("user.dir"));
                			File[] filesList = dir.listFiles();
                            
                        	switch(line) {
                        		case "terminate\n":
                        			terminated = true;
                        			System.out.println("terminating");
                        			break;
                    			
                        		case "list\n":
                        			cchannel.write(encoder.encode(CharBuffer.wrap("<<List begin>>\n")));
                        			
                        			for(File file : filesList) {
                        			    if(file.isFile()) {
                                			cchannel.write(encoder.encode(CharBuffer.wrap(file.getName() + "\n")));
                                		}
                        			}
                        			cchannel.write(encoder.encode(CharBuffer.wrap("<<List end>>\n")));
                        			break;
                        			
                        		default:
                        			if((line.length() > 5) && line.substring(0,4).equals("get ")) {
                        				String filename = line.substring(4);
                        				filename = filename.substring(0, filename.length() - 1);
                        				File thisFile = null;
                            			for(File file : filesList) {
                            			    if(file.isFile() && (file.getName().equals(filename))) {
                            			    	thisFile = file;
                                    		}
                            			}
                        				if(thisFile==null)
                            				cchannel.write(encoder
                            						.encode(CharBuffer.wrap("File " + filename + " not found.\n")));
                        				else {
                        					String newFileName = filename + "-" + cchannel.socket().getPort();
                        					byte[] fileInBytes  = new byte [(int)thisFile.length()];
                        					BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(thisFile));
                        					bufferedInputStream.read(fileInBytes, 0, fileInBytes.length);
                        					ByteBuffer fileByteBuffer = ByteBuffer.wrap(fileInBytes);
                        					
                            				cchannel.write(encoder.encode(CharBuffer.wrap("<<File begin>>\n")));
                            				cchannel.write(encoder.encode(CharBuffer.wrap(newFileName + "\n")));
                            				cchannel.write(fileByteBuffer);
                        					
                        					cchannel.write(encoder.encode(CharBuffer
                        							.wrap("\n<<File end>>\nFile saved in "+ newFileName + " (" + thisFile.length() + "bytes)\n")));
                        					bufferedInputStream.close();
                        				}
                        			} else {
                        				cchannel.write(encoder.encode(CharBuffer.wrap("Unknown Command: " + line)));
                        			}
                        	}
                                
                            // Echo the message back
                            bytesSent = inBuffer.position(); 
                            if (bytesSent != bytesRecv) {
                                System.out.println("write() error, or connection closed");
                                key.cancel();  // deregister the socket
                                continue;
                            }
                         }
                    }
                } // end of while (readyItor.hasNext()) 
            } // end of while (!terminated)
        } catch (IOException e) {
            System.out.println(e);
        }
 
        // close all connections
        Set keys = selector.keys();
        Iterator itr = keys.iterator();
        while (itr.hasNext()) 
        {
            SelectionKey key = (SelectionKey)itr.next();
            if (key.isAcceptable()) {
                ((ServerSocketChannel)key.channel()).socket().close();
            }
            
            else if (key.isValid())
                ((SocketChannel)key.channel()).socket().close();
        }
    }

	public Message getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	public void respond(Message message) {
		// TODO Auto-generated method stub
		
	}
}
