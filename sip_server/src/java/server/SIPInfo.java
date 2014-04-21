package server;


public class SIPInfo {
	public final PacketInfo packetInfo;
  public boolean busy;
  public String sessionId;
  public String senderRtpPort;

  
  public SIPInfo(PacketInfo packetInfo) {
  	this.packetInfo = packetInfo;
  	this.sessionId = packetInfo.sessionId;
  	this.senderRtpPort = packetInfo.senderAddress;
  }
}
