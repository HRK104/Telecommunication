package Version3;


import java.util.logging.Level;
import java.util.logging.Logger;
import tcdIO.Terminal;

public class Demo {

	static final int BROAKER_PORT = 50003;
	static final int DST_PORT_CANDC = 50001;
	static final int DST_PORT_WORKER1 = 50000;
	static final int DST_PORT_WORKER2 = 50002;
	static final String DEFAULT_DST_NODE = "localhost";	
	
	public static void main(String[] args) throws Exception {
		Terminal CandCTerminal= new Terminal("CandC");
		Terminal brokerTerminal= new Terminal("broker");
		Terminal worker1Terminal= new Terminal("Worker1");	
		Terminal worker2Terminal= new Terminal("Worker2");	
		
		Worker1 worker1 = (new Worker1(worker1Terminal, DEFAULT_DST_NODE, BROAKER_PORT, DST_PORT_WORKER1));
		//worker1Terminal.println("Program completed");
			
		Worker2 worker2 = (new Worker2(worker2Terminal, DEFAULT_DST_NODE, BROAKER_PORT, DST_PORT_WORKER2));
		//worker2Terminal.println("Program completed");
		
		Broker broker = (new Broker(brokerTerminal, DEFAULT_DST_NODE, DST_PORT_CANDC, BROAKER_PORT));
		//brokerTerminal.println("Program completed");
		
		CandC candC = (new CandC(CandCTerminal, DEFAULT_DST_NODE, BROAKER_PORT, DST_PORT_CANDC));
		//CandCTerminal.println("Program completed");
			
		//(new Broker(brokerTerminal, DEFAULT_DST_NODE, DST_PORT_CANDC, BROAKER_PORT)).start();
		//brokerTerminal.println("Program completed");
		
		
		
		CandCTerminal.println("Program completed");
		brokerTerminal.println("Program completed");
		worker1Terminal.println("Program completed");
		worker2Terminal.println("Program completed");
	}
	
}





/**
 *
 * @author aran
 */
/*
public class DemoGoBackN {

    public static final int DEMO_CLIENT_PORT = 50100;//50000;
    public static final int DEMO_SERVER_PORT = 50101;//50001;
    public static final String DEMO_HOST = "localhost";    
    
    public static void main(String[] args) {
        
        int windowSize = 4;
        Node.debugMode = true;
        Node.dropRate = 0.20;
        
        Terminal clientTerm =  new Terminal("Client");
        Terminal serverTerm =  new Terminal("Server");
        
        Client client = new Client(clientTerm, DEMO_HOST, DEMO_CLIENT_PORT,
                DEMO_SERVER_PORT , windowSize, windowSize + 1, true);
        Server server = new Server(serverTerm, DEMO_HOST, DEMO_CLIENT_PORT,
                DEMO_SERVER_PORT, 1, windowSize + 1, true);
    }
}
*/