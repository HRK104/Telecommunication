package Version1;
/*
public interface PacketContent {

}*/

import java.net.DatagramPacket;

public interface PacketContent {
	public String toString();
	public DatagramPacket toDatagramPacket();
}