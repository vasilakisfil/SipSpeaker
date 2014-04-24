package server;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SIPMessages {
  static Integer sequence = 2;
  static final Logger logger = LogManager.getLogger(SIPMessages.class.getName());

  public static String RequestType(String line) {
    String type;
    if (line.matches("INVITE sip:.*@.*")) {
      type = "INVITE";
    }
    else if (line.matches("SIP/2.0 200 OK")) {
      type = "OK";
    }
    else if (line.matches("^BYE sip:.* SIP/2.0$")) {
      type = "BYE";
    }
    else if (line.matches("^ACK sip:.* SIP/2.0$")) {
      type = "ACK";
    }
    else if (line.contains("CANCEL")) {
      type = "CANCEL";
    }
    else {
      type = "OTHER";
    }

    return type;
  }

  public static String getSDP(String session_id, String myAddress, String senderRtpPort) {
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
    System.out.println("========== MY SDP ============================");
    System.out.println(sdp_message);
    System.out.println("========== MY SDP ============================");
    return sdp_message;
  }

  public static void Trying(PacketInfo packetInfo) throws UnknownHostException, IOException {
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

  public static void Ringing(PacketInfo packetInfo) throws UnknownHostException, IOException {

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

  public static void Ok(PacketInfo packetInfo) throws UnknownHostException, IOException {

    String sdp_message = SIPMessages.getSDP(packetInfo.sessionId, packetInfo.sipAddress, packetInfo.senderRtpPort);

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
  public static void OkForBye(PacketInfo packetInfo) throws UnknownHostException, IOException {

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

  public static void NotFound(PacketInfo packetInfo) throws UnknownHostException, IOException {
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

  public static void Bye(PacketInfo packetInfo) throws UnknownHostException, IOException {

    String message = "BYE sip:" + packetInfo.senderAddress + " SIP/2.0\r\n"
      + "Via: SIP/2.0/UDP " + packetInfo.sipAddress + ";"
      + "rport;branch=" + packetInfo.branch + "\r\n"
      + "Content-Length: 0\r\n" + //nothing to send additionally
      "Call-ID: " + packetInfo.callId + "\r\n"
      + "CSeq: " + sequence + " BYE\r\n" + //It is the first message sent
      "From: \"" + Configuration.sipUser() + "\"<sip:" + Configuration.sipAddress() + ">;"
        + "tag=" + Configuration.tag() + "\r\n"
      + "Max-Forwards: 70\r\n"
      + "To: <sip:" + packetInfo.senderAddress + ">;tag=" + packetInfo.tag + "\r\n"
      + "User-Agent: SJphone/1.60.299a/L (SJ Labs)\r\n\r\n";

    SIPUtil.SendPacket(message, packetInfo.senderAddress, packetInfo.senderPort);
    logger.debug("Sending Bye to " + packetInfo.senderAddress + ":" + packetInfo.senderPort);
    //removeClient(this);
  }



}
