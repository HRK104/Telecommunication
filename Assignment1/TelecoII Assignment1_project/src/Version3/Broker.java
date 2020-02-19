package Version3;

import java.net.DatagramPacket;

import java.util.ArrayList;
import java.util.Arrays; 
import java.util.Objects;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

import Version3.StringContent;
import tcdIO.*;

public class Broker extends Node{

	static final int DEFAULT_PORT = 50003;
	static final int DST_PORT_CANDC = 50001;
	
	static final int DST_PORT_CANDC2 = 50004;
	
	static final int DST_PORT_WORKER1 = 50000;
	static final int DST_PORT_WORKER2 = 50002;
	static final String DEFAULT_DST_NODE = "localhost";	
	
	int times = -100;
	int workerIndex;
	int index;
	boolean judgeType =false;
	Terminal terminal;
	InetSocketAddress dstAddress;
	DatagramPacket packetFromWorker1 = null;
	DatagramPacket packetFromWorker2 = null;
	ArrayList<DatagramPacket> availableWorkers = new ArrayList<DatagramPacket>();
	
	Broker(Terminal terminal, String dstHost, int dstPort, int srcPort) {
		try {
			this.terminal= terminal;
			dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(srcPort);
			listener.go();
			this.start();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}
	
	public boolean typeOfOrder(String expr) {
		boolean write = false;
		
		if(expr.contains("write")) {
			write = true;
		}
		return write;
	}
	
	
	public int startIndex(String expr) {
		char [] exprChar = expr.toCharArray();
		int result=-100;
		for(int i=0;i<exprChar.length;i++) {
			if(exprChar[i]=='(') {
				result = i;
			}
		}
		return result;
	}
	
	public int endIndex(String expr) {
		char [] exprChar = expr.toCharArray();
		int result = -100;
		for(int i=0;i<exprChar.length;i++) {
			if(exprChar[i]==')') {
				result = i;
			}
		}
		return result;
	}
	
	public String checkTimes(String expr){  // check the times expressed as integer in a String
		
		if(expr == null || expr.isEmpty()) return "";
		
		StringBuilder sb = new StringBuilder();
	    boolean found = false;
	    for(char c : expr.toCharArray()){
	        if(Character.isDigit(c)){
	            sb.append(c);
	            found = true;
	        } else if(found){
	            // If we already found a digit before and this char is not a digit, stop looping
	            break;                
	        }
	    }
	    return sb.toString();
	}
	
	@Override
	public synchronized void onReceipt(DatagramPacket packet) {
		// TODO Auto-generated method stub
		try {
			
			if(packet.getPort()==DST_PORT_WORKER1) {
				//From worker1
				StringContent content= new StringContent(packet);
				terminal.println(content.toString());
				packetFromWorker1 = packet;
				availableWorkers.add(packetFromWorker1);
				
				//for ACK
				DatagramPacket response = null;
				String replyMessage="accept your request";
				content.swap(replyMessage);
				response=content.toDatagramPacket();
				response.setSocketAddress(packetFromWorker1.getSocketAddress());
				socket.send(response);
			}
			else if(packet.getPort()==DST_PORT_WORKER2) {
				//From worker2
				StringContent content= new StringContent(packet);
				terminal.println(content.toString());
				packetFromWorker2 = packet;
				availableWorkers.add(packetFromWorker2);
				
				//for ACK
				DatagramPacket response = null;
				String replyMessage="accept your request";
				content.swap(replyMessage);
				response=content.toDatagramPacket();
				response.setSocketAddress(packetFromWorker2.getSocketAddress());
				socket.send(response);
			}
			
			else {
				//From C&C or C&C2
				index=0;
				workerIndex =0;
				if(packetFromWorker1!=null && packetFromWorker2!=null) {    // there are workable workers in the lists
					
					
					
					
					StringContent content= new StringContent(packet);
					terminal.println(content.toString());
					String replyMessage=null;
                    if(content.toString().equals("confirm job done")) {
						
					}
                    
                    
                    else if(typeOfOrder(content.toString())) {    // if the recieved work description is to print a specific word
						if(startIndex(content.toString())<0 || endIndex(content.toString())<0) replyMessage=null;
						else replyMessage = (content.toString()).substring(startIndex(content.toString())+1, endIndex(content.toString())-1);  // pick up a specific word that should be printed by workers
						times = Integer.parseInt(checkTimes(content.toString()));  //times to print the word
						DatagramPacket response = null;
						content.swap(replyMessage);
						while(index<times) {      // distribute job to each worker
							response=content.toDatagramPacket();
							response.setSocketAddress((availableWorkers.get(workerIndex)).getSocketAddress());
							socket.send(response);
							index++;
							workerIndex++;
							if(workerIndex==availableWorkers.size()) workerIndex =0;
						}
						
						replyMessage = "Job done";
						content.swap(replyMessage);
						terminal.println(replyMessage);
						response=content.toDatagramPacket();
						response.setSocketAddress(packet.getSocketAddress());
						socket.send(response);
					}
					else {
                    	DatagramPacket response = null;
                    	replyMessage = "Job done";
						content.swap(replyMessage);
						terminal.println(replyMessage);
						response=content.toDatagramPacket();
						response.setSocketAddress(packet.getSocketAddress());
						socket.send(response);
                    }
					
				}
				else {                                                     // there are no workable workers in the lists
					String reply = "Incomplete";
					StringContent content= new StringContent(packet);
					terminal.println(content.toString());
					DatagramPacket response;
					response= (new StringContent(reply)).toDatagramPacket();
					response.setSocketAddress(packet.getSocketAddress());
					socket.send(response);
				}
			}
			
		}
		catch(Exception e) {e.printStackTrace();}
	}
	
	
	public void distributeWork(int index, int workerIndex, StringContent content, String replyMessage) throws Exception{
		DatagramPacket response = null;
		content.swap(replyMessage);
		response=content.toDatagramPacket();
		response.setSocketAddress((availableWorkers.get(workerIndex)).getSocketAddress());
		socket.send(response);
	}
	
	public synchronized void start() throws Exception {
		terminal.println("Available worker list: ");
		this.wait();
	}
	
	public static void main(String[] args) {
		try {					
			Terminal terminal= new Terminal("broker");		
			(new Broker(terminal, DEFAULT_DST_NODE, DST_PORT_CANDC, DEFAULT_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}

}
