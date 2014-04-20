package server;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class Configuration {
    
    
    public static String sipUser() {
        return "robot";
    }
    

    public static InetAddress httpInterface() throws UnknownHostException {
        return InetAddress.getByName("127.0.0.1");
    }
    
    public static InetAddress sipInterface() throws UnknownHostException {
      return InetAddress.getByName("127.0.0.1");
    }


    public static int httpPort() {
        return 8080;
    }

    public static int sipPort() {
        return 5061;
    }
    
    public static String messageFile() {
    	return "default.wav";
    }
}
