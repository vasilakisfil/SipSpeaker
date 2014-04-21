package server;

import java.util.ArrayList;

public class VoIPWorker implements Runnable {
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
      WavHandler myWavHandler = new WavHandler();
      myWavHandler.SendWavFile(
      		Configuration.messageFile(),
      		this.client.senderAddress,
      		this.client.senderRtpPort
      );

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
