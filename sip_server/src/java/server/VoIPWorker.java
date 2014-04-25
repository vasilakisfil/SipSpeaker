package server;

import java.util.ArrayList;

import jlibrtp.Participant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VoIPWorker implements Runnable {
  static final Logger logger = LogManager.getLogger(VoIPWorker.class.getName());
  private static ArrayList<PacketInfo> sipClients = new ArrayList<PacketInfo>();
  private PacketInfo client;
  private Boolean busy;

  public VoIPWorker(PacketInfo client) {
    this.client = client;
    this.addClient(client);
  }

  public void run() {
    try {
      this.busy = true;

      SoundSender aDemo = new SoundSender(false, "output.wav");
      Participant p = new Participant(this.client.senderAddress,
      		Integer.parseInt(this.client.senderRtpPort),Integer.parseInt(this.client.senderRtpPort+1));
      aDemo.rtpSession.addParticipant(p);
      aDemo.start();
      try {
          while(aDemo.isAlive()){
              Thread.sleep(800);
              System.out.println("--------->Thread Status " + aDemo.isAlive());
          }
      } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
      }
      //Thread.sleep(5000);//for Basic grade

      this.busy = false;
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    this.removeClient(this.client);
  }

  private void addClient(PacketInfo packetInfo) {
    sipClients.add(packetInfo); 
  }

  private void removeClient(PacketInfo candidateClient) {
    sipClients.remove(candidateClient);
  }

  private PacketInfo getClient(String address) {
    for(PacketInfo obj : sipClients){
      if(obj.senderUsername.equals(address)) {
        return obj;
      }
    }
    return null;
  }

  public static Integer numClients() {
    return sipClients.size();
  }
}
