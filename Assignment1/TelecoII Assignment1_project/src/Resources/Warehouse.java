package Resources;
/*
public class Warehouse {

}*/

public class Warehouse {

	Item[] item;
	
	Warehouse() {
		item= new Item[10];
	}
	
	synchronized public void store(Item item) {
		this.item[0]= item;
	}
	
	synchronized public Item withdraw() {
		return item[0];
	}
	
}
