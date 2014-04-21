package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;


public class Configuration {

  private static String tag = UUID.randomUUID().toString().replace("-", "");

    public static InetAddress httpInterface() throws UnknownHostException {
        return InetAddress.getByName("127.0.0.1");
    }

    public static int httpPort() {
      return 8080;
    }

    public static InetAddress sipInterface() throws UnknownHostException {
      return InetAddress.getByName("127.0.0.1");
    }

    public static String sipUser() {
      return "robot";
    }

    public static int sipPort() {
        return 5061;
    }

    public static String sipAddress() {
      return "127.0.0.1:5061";
    }

    public static String sipFullAddress() {
      return "robot@127.0.0.1:5061";
    }

    public static String messageFile() {
      return "default.wav";
    }

    public static String tag() {
      return tag;
    }
}
