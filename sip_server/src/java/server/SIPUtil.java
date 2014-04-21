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
  
  
  
  
}
