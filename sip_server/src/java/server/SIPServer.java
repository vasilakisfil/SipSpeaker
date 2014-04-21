package server;

//fix imports
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SIPServer extends Thread {
  public static void main(String[] args) throws UnknownHostException, SocketException, IOException, InterruptedException {
    System.out.println("Sip Server listening to :" + Configuration.sipInterface() + "- on port :" + Configuration.sipPort());
    SIPWorker sipWorker = new SIPWorker();
    sipWorker.start();
  }
}
