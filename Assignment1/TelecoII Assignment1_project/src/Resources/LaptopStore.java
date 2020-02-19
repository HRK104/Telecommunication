package Resources;
/*
public class LaptopStore {

}*/

import tcdIO.Terminal;

public class LaptopStore {
	Warehouse warehouse;
	
	LaptopStore(Warehouse warehouse) {
		this.warehouse= warehouse;
	}

	/**
	 * Starts an incoming adn outgoing thread for the store.
	 */
	public void go() {
		Incoming in= new Incoming(warehouse);
		Outgoing out= new Outgoing(warehouse);
		
		in.start();
		out.start();
	}
	
	
	public static void main(String[] args) {
		Warehouse warehouse= new Warehouse();
		LaptopStore store= new LaptopStore(warehouse);
		store.go();
	}

	
	/**
	 * This class handles incoming deliveries and stores them in the warehouse.
	 */
	class Incoming extends Thread {
		Warehouse warehouse;
		
		Incoming(Warehouse warehouse) {
			this.warehouse= warehouse;
		}
		
		public void run() {
			String description;
			Terminal terminal;
			
			// The loop takes a description of an incomnig item and stores 
			// it in the warehouse
			for(;;) {   
				terminal= new Terminal("Incoming Deliveries");
				description= terminal.readString();
				warehouse.store(new Item(description));
			}
		}
	}


	/**
	 * This class handles outgoing sales by withdrawing items from the warehouse.
	 */
	class Outgoing extends Thread {
		Warehouse warehouse;
		
		Outgoing(Warehouse warehouse) {
			this.warehouse= warehouse;
		}
		
		public void run() {
			Item item;
			Terminal terminal;
			String description;
			
			// The loop attempts to withdraw an item from the warehouse
			for(;;) {
				terminal= new Terminal("Outgoing Goods");
				description= terminal.readString("Press Enter");
				item= warehouse.withdraw();
				terminal.println("Item: " +item);
			}
		}
	}
}
