package Version2;

import java.net.DatagramPacket;

public interface PacketContent {
	
	public static byte HEADERLENGTH = 10;
	
	public String toString();
	public DatagramPacket toDatagramPacket();
}
