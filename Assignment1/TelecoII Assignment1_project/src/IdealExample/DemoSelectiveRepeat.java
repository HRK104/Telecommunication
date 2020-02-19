package IdealExample;
import tcdIO.Terminal;
/**
*
* @author aran
*/
public class DemoSelectiveRepeat {

   public static final int DEMO_CLIENT_PORT = 50000;
   public static final int DEMO_SERVER_PORT = 50001;
   public static final String DEMO_HOST = "localhost";    
   
   public static void main(String[] args) {
       
       int windowSize = 4;
       Node.debugMode = true;
       Node.dropRate = 0.20;
       
       Terminal clientTerm =  new Terminal("Client");
       Terminal serverTerm =  new Terminal("Server");
       
       Client client = new Client(clientTerm, DEMO_HOST, DEMO_CLIENT_PORT,
               DEMO_SERVER_PORT, windowSize, 2 * windowSize, false);
       Server server = new Server(serverTerm, DEMO_HOST, DEMO_CLIENT_PORT,
               DEMO_SERVER_PORT, windowSize, 2 * windowSize, false);

   }
}