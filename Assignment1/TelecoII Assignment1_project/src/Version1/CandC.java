package Version1;
/*
public class CandC {

}*/

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import tcdIO.Terminal;

public class CandC extends Node {
	static final int DEFAULT_PORT = 50001;

	Terminal terminal;
	
	/*
	 * 
	 */
	CandC(Terminal terminal, int port) {
		try {
			this.terminal= terminal;
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		try {
			
			String workDoc = "Reply: You can work at college tommorrow";
			
			StringContent content= new StringContent(packet);

			terminal.println(content.toString());
			// You could test here if the String says "end" and terminate the
			// program with a "this.notify()" that wakes up the start() method.
			
			DatagramPacket response;
			response= (new StringContent(workDoc)).toDatagramPacket();
			response.setSocketAddress(packet.getSocketAddress());
			socket.send(response);
		}
		catch(Exception e) {e.printStackTrace();}
	}

	
	public synchronized void start() throws Exception {
		terminal.println("Waiting for contact");
		this.wait();
	}
	
	/*
	 * 
	 */
	public static void main(String[] args) {
		try {					
			Terminal terminal= new Terminal("C&C");
			(new CandC(terminal, DEFAULT_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
