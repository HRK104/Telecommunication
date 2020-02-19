package Version4;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import tcdIO.*;

/**
 *
 * Client class
 * 
 * An instance accepts user input 
 *
 */
public class Worker1 extends Node {
	static final int DEFAULT_SRC_PORT = 50000;
	static final int DEFAULT_DST_PORT = 50003;
	static final String DEFAULT_DST_NODE = "localhost";	
	boolean getBack = false;
	String name;
	Terminal terminal;
	InetSocketAddress dstAddress;
	
	/**
	 * Constructor
	 * 	 
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	public Worker1(Terminal terminal, String dstHost, int dstPort, int srcPort) {
		try {
			this.terminal= terminal;
			dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(srcPort);
			listener.go();
			this.start();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	
	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		
		try {
			StringContent content= new StringContent(packet);
			this.notify();
			if(content.toString().equals("accept your request")) {        // worker's request is accepted by the broker
				getBack=true;
				terminal.println("get reply from broker");
			}
			else if(getBack) {                                            // get the word to print
				terminal.println("\n");  
				terminal.println(content.toString());
				
				//for ACK
				DatagramPacket response = null;
				String replyMessage="task done";
				content.swap(replyMessage);
				response=content.toDatagramPacket();
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);
			}
			else {                                                        // get other types of messages, restart again
				start();
			}
		}
		catch(Exception e) {e.printStackTrace();}
		
	}

	
	/**
	 * Sender Method
	 * 
	 */
	
	public synchronized void start() throws Exception {
		if(!getBack) {
			
			DatagramPacket packet= null;

			byte[] payload= null;
			byte[] header= null;
			byte[] buffer= null;
			
			    payload= (terminal.readString("If you are volunteering for work, enter your name: \n")).getBytes();
				header= new byte[PacketContent.HEADERLENGTH];

				buffer= new byte[header.length + payload.length];
				System.arraycopy(header, 0, buffer, 0, header.length);
				System.arraycopy(payload, 0, buffer, header.length, payload.length);
				
				terminal.println("Sending packet...");
				packet= new DatagramPacket(buffer, buffer.length, dstAddress);
				socket.send(packet);
				terminal.println("Packet sent");
				this.wait();
		}
		
	}
	
	
	/**
	 * Test method
	 * 
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
		try {					
			Terminal terminal= new Terminal("Worker1");		
			(new Worker1(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
			//terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}


