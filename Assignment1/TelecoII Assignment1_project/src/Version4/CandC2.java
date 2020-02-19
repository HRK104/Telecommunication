package Version4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import tcdIO.Terminal;

public class CandC2 extends Node {
	static final int DEFAULT_SRC_PORT = 50004;
	static final int DEFAULT_DST_PORT = 50003;
	static final String DEFAULT_DST_NODE = "localhost";	
	boolean getBack = false;
	Terminal terminal;
	InetSocketAddress dstAddress;
	
	
	
	CandC2(Terminal terminal, String dstHost, int dstPort, int srcPort) {
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
	public void onReceipt(DatagramPacket packet) {
		try {
			StringContent content= new StringContent(packet);
			
			if(content.toString().equals("Incomplete")) {
				terminal.println("Fail to send. Start again");
				this.start();
			}
			else if(content.toString().equals("Job done")) {
				terminal.println("accept job done");
				//for ACK
				DatagramPacket response = null;
				String replyMessage="accept job done";
				content.swap(replyMessage);
				response=content.toDatagramPacket();
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);
			}
			else {
				this.wait();
			}
			
		}
		catch(Exception e) {e.printStackTrace();}
	}

	
	public synchronized void start() throws Exception {
		
		DatagramPacket packet= null;

		byte[] payload= null;
		byte[] header= null;
		byte[] buffer= null;
		
		    payload= (terminal.readString("Enter work discription\n"+"(e.g) write ( 'word' ) 'number' times  //change within ' '\n")).getBytes();
		    

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
	
	/*
	 * 
	 */
	public static void main(String[] args) {
		try {					
			Terminal terminal= new Terminal("CandC2");
			(new CandC(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}



