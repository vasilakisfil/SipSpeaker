package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class SIPWorker {
  static final Logger logger = LogManager.getLogger(SIPWorker.class.getName());
  private static ArrayList<PacketInfo> sipCandidateClients;
  public static DatagramSocket serverSocket;  //The main socket to listen on
  //For sending and receiving via UDP
  private DatagramPacket receivePacket;


  public SIPWorker() throws SocketException, UnknownHostException {
    sipCandidateClients = new ArrayList<PacketInfo>();

    SIPWorker.serverSocket = new DatagramSocket(Configuration.sipPort(), Configuration.sipInterface());    
  }

  public void start() throws IOException, InterruptedException {

    while (true) {
      logger.debug("Concurrent clients:" + VoIPWorker.numClients());

      receivePacket = SIPUtil.getPacket();
      String received = new String(receivePacket.getData(), 0, receivePacket.getLength());

      logger.debug("Received:\n" + received);
      PacketInfo packetInfo = SIPUtil.parseUDP(received);
      packetInfo.senderPort = receivePacket.getPort();

      switch (SIPMessages.RequestType(packetInfo.statusLine[0])) {
        case "INVITE":
          this.processInvite(packetInfo);
          break;
        case "OK":
          System.out.println("200 OK received!");
          break;
        case "CANCEL":
          System.out.println("CANCEL received!");
          break;
        case "BYE":
          System.out.println("BYE received!");
          //removeClient(this);
          SIPMessages.OkForBye(packetInfo);
          break;
        case "ACK":
          this.processAck(packetInfo);
          break;
      }
    }
  }



  private void processInvite(PacketInfo packetInfo) throws UnknownHostException, IOException, InterruptedException {
    this.addCandidateClient(packetInfo);

    if (packetInfo.receiverUser.equals(Configuration.sipUser())) {
      logger.debug("Trying");
      SIPMessages.Trying(packetInfo);
      Thread.sleep(100);//wait a little for ringing in softphone
      //send ringing
      logger.debug("Ringing");
      SIPMessages.Ringing(packetInfo);
      Thread.sleep(100);//wait a little for ringing in softphone
      //send OK
      logger.debug("OK");
      SIPMessages.Ok(packetInfo);
    } else {
      SIPMessages.NotFound(packetInfo);
    }
  }

  private void processAck(PacketInfo packetInfo) throws UnknownHostException, IOException {
    logger.debug("ACK received! " + packetInfo.receiverUser);
    if ((packetInfo.receiverUser.equals(Configuration.sipUser()))) {
      PacketInfo client = this.getCandidateClient(packetInfo.senderUsername);
      if (client != null) {
        this.removeCandidateClient(client);
        VoIPWorker voipWorker = new VoIPWorker(client);
        voipWorker.run();
      }
      SIPMessages.Bye(packetInfo);
    }
    System.out.println("Clients connected now:" + VoIPWorker.numClients());

  }


  private void addCandidateClient(PacketInfo packetInfo) {
    if (this.getCandidateClient(packetInfo.senderAddress) == null) {
      sipCandidateClients.add(packetInfo);
    }
  }

  private void removeCandidateClient(PacketInfo candidateClient) {
    sipCandidateClients.remove(candidateClient);
  }

  private PacketInfo getCandidateClient(String address) {
    for(PacketInfo obj : sipCandidateClients){
      if(obj.senderUsername.equals(address)) {
        return obj;
      }
    }
    return null;
  }
}
