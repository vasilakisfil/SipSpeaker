package server;

//fix imports
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.apache.logging.log4j.*;

public class SIPServer {
  static final Logger logger = LogManager.getLogger(SIPServer.class.getName());
  
  public static void main(String[] args) throws UnknownHostException, SocketException, IOException, InterruptedException {
    logger.info("Sip Server listening to :" + Configuration.sipInterface() + "- on port :" + Configuration.sipPort());
    SIPWorker sipWorker = new SIPWorker();
    sipWorker.start();
  }
}
