package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class SIPWorker {
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
      System.out.println("Concurrent clients:" + VoIPWorker.numClients());

      receivePacket = SIPUtil.getPacket();
      String received = new String(receivePacket.getData(), 0, receivePacket.getLength());

      System.out.println("Received:\n" + received);
      PacketInfo packetInfo = SIPUtil.parseUDP(received);
      packetInfo.senderPort = receivePacket.getPort();

      switch (SIPMessages.RequestType(packetInfo.statusLine[0])) {
        case "INVITE":
          this.addCandidateClient(packetInfo);

          String RcvSocketAddr = receivePacket.getSocketAddress().toString();
          System.out.println("INVITE received from: " + RcvSocketAddr);

          System.out.println("In request:" + packetInfo.receiverUser + "\n In parameters:" + Configuration.sipUser() + "|");
          if (packetInfo.receiverUser.equals(Configuration.sipUser())) {
            //if (checkForDialogSession()) {//other dialog session exists with same From-To-Call_ID
             //   System.out.println("Session with client already started");
            //    break;
            //}
            //send trying
            System.out.println("Trying");
            SIPMessages.Trying(packetInfo);
            Thread.sleep(100);//wait a little for ringing in softphone
            //send ringing
            System.out.println("Ringing");
            SIPMessages.Ringing(packetInfo);
            Thread.sleep(100);//wait a little for ringing in softphone
            //send OK
            System.out.println("OK");
            SIPMessages.Ok(packetInfo);
          } else {
            SIPMessages.NotFound(packetInfo);
          }
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
          System.out.println("ACK received! " + packetInfo.receiverUser);
          if ((packetInfo.receiverUser.equals(Configuration.sipUser()))) {
            PacketInfo client = this.getCandidateClient(packetInfo.senderUsername);
            if (client != null) {
              this.removeCandidateClient(client);
              VoIPWorker voipWorker = new VoIPWorker(client);
              voipWorker.run();
            }
            SIPMessages.Bye(packetInfo);
          }
          System.out.print("Clients connected now:" + VoIPWorker.numClients());
          break;
      }
    }
  }

  private void addCandidateClient(PacketInfo packetInfo) {
    sipCandidateClients.add(packetInfo);  
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
