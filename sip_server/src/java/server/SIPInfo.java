package server;


public class SIPInfo {
  public String receiverUser;  //The user to which the caller wants to speak
  public String tag;

  public String Call_ID;           //Call-ID contains a globally unique identifier for this call

  public String senderAddress;  //IPv4 address of the sender as a string
  public String senderUsername;  //Username from whom the call is received
  public int senderPort;          //the sender is using that port

  public String senderRtpPort;  //Port on which to transmit RTP

  public String branch;
}
