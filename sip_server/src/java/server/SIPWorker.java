package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;


public class SIPWorker {
  public static ArrayList<SIPInfo> sipservers;
  public static int client_counter = 0;
  public static DatagramSocket serverSocket;  //The main socket to listen on
  //For sending and receiving via UDP
  private DatagramPacket receivePacket;
  private DatagramPacket SendPacket;
  
  public static String session_id;
  public static String senderRtpPort;
  
  
  public SIPWorker() throws SocketException, UnknownHostException {
    sipservers = new ArrayList();
	  this.serverSocket = new DatagramSocket(Configuration.sipPort(), Configuration.sipInterface());    
  }
	
	public void start() throws IOException, InterruptedException {
    
    while (true) {
      System.out.println("Concurrent clients:" + VoIPWorker.getClients());
      

      receivePacket = SIPUtil.getPacket();
      String received = new String(receivePacket.getData(), 0, receivePacket.getLength());
      
      System.out.println("Received:\n" + received);
      PacketInfo packetInfo = SIPUtil.parseUDP(received);
      packetInfo.senderPort = receivePacket.getPort();
      
      switch (SIPUtil.RequestType(packetInfo.parse_result[0])) {//first line contains the request type of the header
          case 0://INVITE
              String RcvSocketAddr = receivePacket.getSocketAddress().toString();
              System.out.println("INVITE received from: " + RcvSocketAddr);
              //Check if call is for me
              System.out.println("In request:" + packetInfo.receiverUser + "\n In parameters:" + Configuration.sipUser() + "|");
              if (packetInfo.receiverUser.equals(Configuration.sipUser())) {
                  //if (checkForDialogSession()) {//other dialog session exists with same From-To-Call_ID
                   //   System.out.println("Session with client already started");
                  //    break;
                  //}
                  //send trying
                  System.out.println("Trying");
                  SIPUtil.TryingMessage(packetInfo);
                  Thread.sleep(100);//wait a little for ringing in softphone
                  //send ringing
                  System.out.println("Ringing");
                  SIPUtil.RingingMessage(packetInfo);
                  Thread.sleep(100);//wait a little for ringing in softphone
                  //send OK
                  System.out.println("OK");
                  SIPUtil.OKMessage(packetInfo);
              } else {
                  SIPUtil.NotFoundMessage(packetInfo);
              }
              break;
          case 1://OK
              System.out.println("200 OK received!");
              break;
          case -1://Cancel received
              System.out.println("CANCEL received!");
              break;
          case 2://BYE
              System.out.println("BYE received!");
              //removeClient(this);
              SIPUtil.OKafterBYEMessage(packetInfo);
              break;
          case 3://ACK
              System.out.println("ACK received! " + packetInfo.receiverUser);
              if ((packetInfo.receiverUser.equals(Configuration.sipUser()))) {
                  SIPServer newsipserver = new SIPServer();
                  newsipserver = new SIPServer();
                  SIPInfo sipInfo = new SIPInfo();
                  sipInfo.receiverUser = packetInfo.receiverUser;
                  sipInfo.tag = packetInfo.tag;

                  sipInfo.Call_ID = packetInfo.Call_ID;
                  sipInfo.senderAddress = packetInfo.senderAddress;
                  sipInfo.senderUsername = packetInfo.senderUsername;
                  sipInfo.senderPort = packetInfo.senderPort;

                  sipInfo.senderRtpPort = senderRtpPort;

                  sipInfo.branch = packetInfo.branch;

                  //sipservers.get(client_counter).start();
                  VoIPWorker voipWorker = new VoIPWorker(sipInfo);
                  this.sipservers.add(sipInfo);
                  voipWorker.run();
                  
                  SIPUtil.SendByeMessage(packetInfo);
                  client_counter++;


              }
              System.out.print("Clients connected now: ");
              System.out.println(client_counter);

              break;
      }
    }		
	}
}
