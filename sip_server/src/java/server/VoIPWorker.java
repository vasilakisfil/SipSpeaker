package server;

public class VoIPWorker implements Runnable {
	private static Integer clients = 0;
	private SIPInfo sipInfo;
	private Boolean busy;

	public VoIPWorker(SIPInfo sipInfo) {
		this.sipInfo = sipInfo;
		this.increaseClients();
	}

	public void run() {
    try {
      this.sipInfo.busy = true;
      WavHandler myWavHandler = new WavHandler();
      myWavHandler.SendWavFile(Configuration.messageFile(), this.sipInfo.senderAddress, this.sipInfo.senderRtpPort);
      //Thread.sleep(5000);//for Basic grade

      this.sipInfo.busy = false;
	  } catch (Exception ex) {
	      ex.printStackTrace();
	  }
    this.decreaseClients();
	}

	public static Integer getClients() {
		return clients;
	}

	public static void increaseClients() {
		VoIPWorker.clients++;
	}
	
	public static void decreaseClients() {
		VoIPWorker.clients--;
	}


}
