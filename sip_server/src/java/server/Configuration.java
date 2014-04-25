package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;


public class Configuration {

  private static String tag = UUID.randomUUID().toString().replace("-", "");

  private static String sipInterface;
  private static String sipUser;
  private static Integer sipPort;

    public static InetAddress sipInterface() throws UnknownHostException {
      return InetAddress.getByName(sipInterface);
    }
    
    public static String sipInterfaceStr() throws UnknownHostException {
      return InetAddress.getByName(sipInterface).toString().replace("/", "");
    }

    public static void sipInterface(String ip) {
      Configuration.sipInterface = ip;
    }

    public static String sipUser() {
      return sipUser;
    }

    public static void sipUser(String user) {
      Configuration.sipUser = user;
    }

    public static Integer sipPort() {
        return sipPort;
    }

    public static void sipPort(String port) {
      Configuration.sipPort = Integer.parseInt(port);
    }

    public static String sipAddress() {
      return sipInterface + ":" + sipPort;
    }

    public static String sipFullAddress() {
      return sipUser + "@" + sipAddress();
    }

    public static String messageFile() {
      return "output.wav";
    }

    public static String tag() {
      return tag;
    }
}
