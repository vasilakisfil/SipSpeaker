package server;

//fix imports
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SIPServer extends Thread {

    public static void main(String[] args) throws UnknownHostException, SocketException, IOException, InterruptedException {

        System.out.println("Sip Server listening to :" + Configuration.sipInterface() + "- on port :" + Configuration.sipPort());
        
        SIPWorker sipWorker = new SIPWorker();
        sipWorker.start();

    }


/*
    private boolean checkForDialogSession() {
        if (sipservers.isEmpty()) {
            return false;
        }
        for (int i = 0; i < client_counter; i++) {
            if (sipservers.get(i).receiverUser.equals(this.receiverUser) && sipservers.get(i).Call_ID.equals(this.Call_ID) && sipservers.get(i).senderUsername.equals(this.senderUsername) && sipservers.get(i).busy) {
                return true; //there is a Call existing with same From-To-CallID
            }
        }
        return false;
    }

    private void removeClient(SIPServer a) {
        if (!sipservers.isEmpty()) {

            for (int i = 0; i < sipservers.size(); i++) {
                System.out.println(sipservers.get(i).receiverUser + (this.receiverUser) + sipservers.get(i).Call_ID + (this.Call_ID) + sipservers.get(i).senderUsername + (this.senderUsername));
                if (sipservers.get(i).receiverUser.equals(this.receiverUser) && sipservers.get(i).Call_ID.equals(this.Call_ID) && sipservers.get(i).senderUsername.equals(this.senderUsername)) {
                    if (sipservers.get(i).busy) {
                        //sipservers.get(i).interrupt();
                    }

                    sipservers.remove(i);
                }
            }
            client_counter--;
        } else {
            this.interrupt();
            client_counter = 0;
        }
    }
    */
}
