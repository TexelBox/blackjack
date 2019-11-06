package server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import client.View;

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
	public static List<Socket> gui = new ArrayList<Socket>();
	public Parser parser;

	public API() throws Exception {
		this.parser = new Parser();
		System.out.println("Welcome to blackjack");

		// Initialize buffers and coders for channel receive and send
		String line = "";
		Charset charset = Charset.forName( "us-ascii" );  
		CharsetDecoder decoder = charset.newDecoder();  
		CharsetEncoder encoder = charset.newEncoder();
		ByteBuffer inBuffer = null;
		CharBuffer cBuffer = null;
		int bytesSent, bytesRecv;     // number of bytes sent or received

		int portNum = 9000;

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

							switch(line) {
							case "terminate\n":
								terminated = true;
								System.out.println("terminating");
								break;

								// initalizes the GUI
							case "<<GUI>>\n":
								if(!gui.contains(socket))
									gui.add(socket); // bind

									socket.getChannel().write(encoder.encode(CharBuffer.wrap(View.getStateUI(this.parser))));
									break;

							default:
								switch(line.substring(0, 3)) {
								case "/t ":
									for(Socket guiSocket: gui) {
										guiSocket.getChannel().write(encoder.encode(CharBuffer.wrap("<<TXT>>" + line.substring(3) + "\n")));
									}
									break;

								case "/a ":
									if(this.parser.authenticate(line.substring(3))) {
										if(this.parser.setUser(line.substring(3))==-1)
											cchannel.write(encoder.encode(CharBuffer.wrap("full" + "\n")));
										else
											cchannel.write(encoder.encode(CharBuffer.wrap("ok" + "\n")));
									} else cchannel.write(encoder.encode(CharBuffer.wrap("no" + "\n")));
									break;
								case "/b ":
									String input = this.parser.betToParserInput(line);
									if(this.parser.errorCheck(input)) {
										this.parser.actionTaken(input);
										cchannel.write(encoder.encode(CharBuffer.wrap("ok" + "\n")));
									}else cchannel.write(encoder.encode(CharBuffer.wrap("no" + "\n")));
									break;
								case "/h ":
								case "/s ":
								case "/d ":
									input = this.parser.lineToParserInput(line);
									if(this.parser.errorCheck(input)) {
										this.parser.actionTaken(input);
										cchannel.write(encoder.encode(CharBuffer.wrap("ok" + "\n")));
									}else cchannel.write(encoder.encode(CharBuffer.wrap("no" + "\n")));
									break;
								case "/j ":
								case "/q ":
									// action happens
									// logic goes here

									// make sure everything gets processed beforehand here
								default:
								}
							}

							String UIState = View.getStateUI(this.parser);
							for(Socket guiSocket: gui) {
								guiSocket.getChannel().write(encoder.encode(CharBuffer.wrap(UIState + "\n")));
							}

							// Echo the message back
							bytesSent = inBuffer.position(); 
							if (bytesSent != bytesRecv) {
								System.out.println("write() error, or connection closed");
								key.cancel();  // deregister the socket
								if(gui.contains(socket))
									gui.remove(socket);
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
}
