package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SIPUtil {

  public static int RequestType(String line) {
    Integer type;
    if (line.matches("INVITE sip:.*@.*")) {
      type = 0;
    }
    else if (line.matches("SIP/2.0 200 OK")) {
      type = 1;
    }
    else if (line.matches("^BYE sip:.* SIP/2.0$")) {
      type = 2;
    }
    else if (line.matches("^ACK sip:.* SIP/2.0$")) {
      type = 3;
    }
    else if (line.contains("CANCEL")) {
      type = -1;
    }
    else {
      type = 4;
    }

    return type;
  }

  public static String createSDPMessage(String session_id, String myAddress, String senderRtpPort) {
    String sdp_message = "v=0\r\n"
      + "o=- " + session_id + " IN IP4 " + myAddress + "\r\n"
      + "s=SJphone\r\n"
      + "c=IN IP4 " + myAddress + "\r\n"
      + "t=0 0\r\n"
      + "m=audio " + senderRtpPort + " RTP/AVP 3 101\r\n"
      + "a=sendrecv\r\n"
      + "a=rtpmap:3 GSM/8000\r\n"
      + "a=rtpmap:101 telephone-event/8000\r\n"
      + "a=fmtp:101 0-11,16\r\n";
    return sdp_message;
  }

  public static void SendPacket(String message, String senderAddress, int senderPort)
      throws UnknownHostException, IOException {
    System.out.println("Sending packet...:\n");
    System.out.println(message);
    byte[] sendData = new byte[1024];
    sendData = message.getBytes();
    DatagramPacket SendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(senderAddress), senderPort);
    SIPWorker.serverSocket.send(SendPacket);
  }


  public static DatagramPacket getPacket() throws IOException {
    byte[] receiveData;
    receiveData = new byte[1024];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    SIPWorker.serverSocket.receive(receivePacket);
    return receivePacket;
  }


  public static void TryingMessage(PacketInfo packetInfo) throws UnknownHostException, IOException {
    String message = "SIP/2.0 100 Trying\r\n"
      + "Via: SIP/2.0/UDP " + packetInfo.senderAddress + ";"
      + "rport=" + Configuration.sipPort() + ";"
      + "received=" + packetInfo.senderAddress + ";"
      + "branch=" + packetInfo.branch + "\r\n"
      + "Content-Length: 0\r\n"
      + "Contact: <sip:" + Configuration.sipAddress() + ">\r\n"
      + "Call-ID: " + packetInfo.callId + "\r\n"
      + "CSeq: 1 INVITE\r\n"
      + "From: \"" + packetInfo.senderUsername + "\"<sip:" + packetInfo.senderAddress + ">;tag=" + packetInfo.tag + "\r\n"
      + "To: \"" + Configuration.sipUser() + "\"<sip:" + Configuration.sipFullAddress() + ">;"
        + "tag=" + Configuration.tag() + "\r\n\r\n";

    SIPUtil.SendPacket(message, packetInfo.senderAddress, packetInfo.senderPort);

  }

  public static void RingingMessage(PacketInfo packetInfo) throws UnknownHostException, IOException {

      String message = "SIP/2.0 180 Ringing\r\n"
        + "Via: SIP/2.0/UDP " + packetInfo.senderAddress + ";"
        + "rport=" + Configuration.sipPort() + ";"
        + "received=" + packetInfo.senderAddress + ";"
        + "branch=" + packetInfo.branch + "\r\n"
        + "Content-Length: 0\r\n"
        + "Contact: <sip:" + Configuration.sipAddress() + ">\r\n"
        + "Call-ID: " + packetInfo.callId + "\r\n"
        + "CSeq: 1 INVITE\r\n"
        + "From: \"" + packetInfo.senderUsername + "\"<sip:" + packetInfo.senderAddress + ">;tag=" + packetInfo.tag + "\r\n"
        + "To: \"" + Configuration.sipUser() + "\"<sip:" + Configuration.sipFullAddress() + ">;"
          + "tag=" + Configuration.tag() + "\r\n\r\n";

      SIPUtil.SendPacket(message, packetInfo.senderAddress, packetInfo.senderPort);
  }

  public static void OKMessage(PacketInfo packetInfo) throws UnknownHostException, IOException {

      String sdp_message = SIPUtil.createSDPMessage(packetInfo.sessionId, packetInfo.sipAddress, packetInfo.senderAddress);

      String message = "SIP/2.0 200 OK\r\n"
        + "Via: SIP/2.0/UDP " + packetInfo.senderAddress + ";"
        + "rport=" + Configuration.sipPort() + ";received=" + packetInfo.senderAddress + ";"
        + "branch=" + packetInfo.branch + "\r\n"
        + "Content-Length: " + sdp_message.length() + "\r\n"
        + "Contact: <sip:" + Configuration.sipAddress() + ">\r\n"
        + "Call-ID: " + packetInfo.callId + "\r\n"
        + "Content-Type: application/sdp\r\n"
        + "CSeq: 1 INVITE\r\n"
        + "From: \"" + packetInfo.senderUsername + "\"<sip:" + packetInfo.senderAddress + ">;tag=" + packetInfo.tag + "\r\n"
        + "To: \"" + Configuration.sipUser() + "\"<sip:" + Configuration.sipFullAddress() + ">;"
          + "tag=" + Configuration.tag() + "\r\n\r\n"
        + sdp_message;

      SIPUtil.SendPacket(message, packetInfo.senderAddress, packetInfo.senderPort);
  }
  public static void OKafterBYEMessage(PacketInfo packetInfo) throws UnknownHostException, IOException {

      String message = "SIP/2.0 200 OK\r\n"
        + "Via: SIP/2.0/UDP " + packetInfo.senderAddress + ";\r\n"
        + "branch=" + packetInfo.branch + "\r\n"
        + "Call-ID: " + packetInfo.callId + "\r\n"
        + "CSeq: "+packetInfo.cSeq+" BYE\r\n"
        + "From: \"" + packetInfo.senderUsername + "\"<sip:" + packetInfo.senderAddress + ">;tag=" + packetInfo.tag + "\r\n"
        + "To: \"" + Configuration.sipUser() + "\"<sip:" + Configuration.sipFullAddress() + ">;"
          + "tag=" + Configuration.tag() + "\r\n\r\n";

      SIPUtil.SendPacket(message, packetInfo.senderAddress, packetInfo.senderPort);
  }

  public static void NotFoundMessage(PacketInfo packetInfo) throws UnknownHostException, IOException {
      String message = "SIP/2.0 404 Not Found\r\n"
        + "Via: SIP/2.0/UDP " + packetInfo.senderAddress + ";"
        + "rport=" + Configuration.sipPort() + ";received=" + packetInfo.senderAddress + ";"
        + "branch=" + packetInfo.branch + "\r\n"
        + "Content-Length: 0\r\n"
        + "Contact: <sip:" + Configuration.sipAddress() + ">\r\n"
        + "Call-ID: " + packetInfo.callId + "\r\n"
        + "CSeq: 1 INVITE\r\n"
        + "From: \"" + packetInfo.senderUsername + "\"<sip:" + packetInfo.senderAddress + ">;tag=" + packetInfo.tag + "\r\n"
        + "To: \"" + Configuration.sipUser() + "\"<sip:" + Configuration.sipFullAddress() + ">;"
          + "tag=" + Configuration.tag() + "\r\n\r\n";

      SIPUtil.SendPacket(message, packetInfo.senderAddress, packetInfo.senderPort);
  }

  public static void SendByeMessage(PacketInfo packetInfo) throws UnknownHostException, IOException {

      int seq;//=Integer.parseInt(CSeq);
      //seq++; //increment CSeq for the BYE transaction
      seq = 2;
      String message = "BYE sip:" + packetInfo.senderAddress + " SIP/2.0\r\n"
        + "Via: SIP/2.0/UDP " + packetInfo.sipAddress + ";"
        + "rport;branch=" + packetInfo.branch + "\r\n"
        + "Content-Length: 0\r\n" + //nothing to send additionally
        "Call-ID: " + packetInfo.callId + "\r\n"
        + "CSeq: " + seq + " BYE\r\n" + //It is the first message sent
        "From: \"" + Configuration.sipUser() + "\"<sip:" + Configuration.sipAddress() + ">;"
          + "tag=" + Configuration.tag() + "\r\n"
        + "Max-Forwards: 70\r\n"
        + "To: <sip:" + packetInfo.senderAddress + ">;tag=" + packetInfo.tag + "\r\n"
        + "User-Agent: SJphone/1.60.299a/L (SJ Labs)\r\n\r\n";

      SIPUtil.SendPacket(message, packetInfo.senderAddress, packetInfo.senderPort);
      System.out.print("Sending Bye to " + packetInfo.senderAddress + ":" + packetInfo.senderPort);
      //removeClient(this);
  }


  public static PacketInfo parseUDP(String packet) {
    PacketInfo packetInfo = new PacketInfo();
    List<String> SIPRequest = new ArrayList<String>();
    int sdp_length = 0;
    StringTokenizer st = new StringTokenizer(packet, "\r\n");
    String line = null;

    while (st.hasMoreTokens()) {
        line = st.nextToken();
        SIPRequest.add(line);
        if (line.matches("INVITE sip:.*@.*")) {
          packetInfo.sipAddress = line.substring(line.indexOf("@") + 1, line.indexOf(" SIP/2.0"));
        }
        if (line.matches("^Via: SIP/2\\.0/UDP .*")) {
          packetInfo.branch = line.split("branch=")[1];
          packetInfo.viaAddress = line.substring(("Via: SIP/2.0/UDP ").length(), line.indexOf(";"));

        }
        if (line.startsWith("To: <")) {
          packetInfo.receiverUser = line.split("@")[0].split("sip:")[1];
            System.out.println("To-------------------" + packetInfo.receiverUser);
        }
        if (line.startsWith("From")) {
            packetInfo.senderUsername = line.split("From: \"")[1].split("\"")[0];
            packetInfo.senderAddress = (line.split("<sip:")[1]).split(">")[0];
            System.out.println("username:" + packetInfo.senderUsername + "\naddress" + packetInfo.senderAddress);
            packetInfo.tag = line.split("tag=")[1];
        }

        if (line.matches("Call-ID: .*")) {
          packetInfo.callId = line.substring(("Call-ID: ").length(), line.length());
        }
        if (line.matches("CSeq: .*")) {
          packetInfo.cSeq = line.split(" ")[1];

        }
        if (line.matches("^o=- .* IN IP4 .*$")) {
          //SIPWorker.session_id = (line.split("=- ")[1]).split(" IN ")[0];
        	packetInfo.sessionId = (line.split("=- ")[1]).split(" IN ")[0];
        }
        if (line.matches("^m=audio .* RTP.*$")) {
          //SIPWorker.senderRtpPort = (line.split("audio ")[1]).split(" ")[0];
          packetInfo.senderRtpPort = (line.split("audio ")[1]).split(" ")[0];
        }
    }
    packetInfo.statusLine = SIPRequest.toArray(new String[SIPRequest.size()]);
    return packetInfo;
  }
}
