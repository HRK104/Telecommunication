package IdealExample;
/*
public class Node {

}*/
//package cs.tcd.ie;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import tcdIO.Terminal;
public abstract class Node {

    Sender sender;
    LinkedBlockingQueue<PacketContent> receiver;
    
    Terminal terminal;

    public static boolean debugMode = true;
    public static double dropRate = 0.15;
    
    static final int PACKETSIZE = 65536;
    
    DatagramSocket socket;
    Listener listener;
    CountDownLatch latch;
    
    Node() {
        latch= new CountDownLatch(1);
        listener= new Listener();
        listener.setDaemon(true);
        listener.start();
    }

     /**
      * Action to take upon receiving datagram packet.
      * 
      * @param packet - Stores received packet
      */
    public abstract void onReceipt(DatagramPacket packet);


    /**
     * Action to take upon packets entering the received buffer.
     * 
     */
    public abstract void packetReady();
    
    /**
     * Puts the packet in the send buffer to await being sent.
     * 
     * @param content
     */    
    public void bufferPacket(PacketContent content) {
        sender.add(content);
    }  
    
    /**
     * Attempts to get an input packet.
     * 
     * This must be a blocking method.
     * 
     * @return the next input packet
     */
    public abstract PacketContent getPacket();
    
    /**
     * Sends the packet immediately.
     * 
     * 'dropRate' chance the packet will not send.
     * 
     * @param packet
     */
    public void sendPacket(DatagramPacket packet) {
        boolean drop = new Random().nextDouble() < dropRate;
        if (!drop) { 
            try {
                socket.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (debugMode) {
            PacketContent content = PacketContent.fromDatagramPacket(packet);
            terminal.println("Sending " + content.getPacketNumber() + ": " + content);
            if (drop) {
                terminal.println(" ... Dropped!");
            } else {
                terminal.println(" ... Sent!");
            }
        }
    }  
    
    /**
     * Listener thread.
     * 
     * Listens for incoming packets on a datagram socket and informs 
     * registered receivers about incoming packets.
     */
    class Listener extends Thread {

        /*
         *  Telling the listener that the socket has been initialized 
         */
        public void go() {
            latch.countDown();
        }

        /*
         * Listen for incoming packets and inform receivers
         */
        @Override
        public void run() {
            try {
                latch.await();
                // Endless loop: attempt to receive packet, notify receivers, etc
                while(true) {
                    DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
                    socket.receive(packet);
                    if (debugMode) {
                        PacketContent content = PacketContent.fromDatagramPacket(packet);
                        terminal.println("Recieved " + content.getPacketNumber() + ": " + content);
                    }
                    onReceipt(packet);
                }
            } catch (Exception e) {if (!(e instanceof SocketException)) e.printStackTrace();}
        }
    }
}

