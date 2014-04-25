package server;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.lang.String;
import java.net.DatagramSocket;
import java.util.Enumeration;
import java.util.ResourceBundle.Control;

import jlibrtp.*;

public class SoundSender extends Thread implements RTPAppIntf  {
    public RTPSession rtpSession = null;
    int pktCount = 0;
    static int dataCount = 0;
    private String filename;
    private final int EXTERNAL_BUFFER_SIZE = 1024;
    SourceDataLine auline;
    private Position curPosition;
    boolean local;
    enum Position {
        LEFT, RIGHT, NORMAL
    };

    public SoundSender(boolean isLocal, String file)  {
        this.filename = file;

        try {
            DatagramSocket rtpSocket = new DatagramSocket(16986);
            DatagramSocket rtcpSocket = new DatagramSocket(1687);


            rtpSession = new RTPSession(rtpSocket, rtcpSocket);
            rtpSession.RTPSessionRegister(this,null, null);
            System.out.println("CNAME: " + rtpSession.CNAME());

        } catch (Exception e) {
            System.out.println(e);
            System.out.println("RTPSession failed to obtain port");
        }


        this.local = isLocal;
    }

    public void receiveData(DataFrame dummy1, Participant dummy2) {
        // We don't expect any data.
    }

    public void userEvent(int type, Participant[] participant) {
        //Do nothing
    }

    public int frameSize(int payloadType) {
        return 1;
    }

    @Override
    public void run() {

        if(RTPSession.rtpDebugLevel > 1) {
            System.out.println("-> Run()");
        }
        File soundFile = new File(filename);
        if (!soundFile.exists()) {
            System.err.println("Wave file not found: " + filename);
            return;
        }

        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (UnsupportedAudioFileException e1) {
            e1.printStackTrace();
            return;
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }

        AudioFormat format = audioInputStream.getFormat();
        System.out.println("This is the AUDIO FORMAT ORIGINAL ---->" + format.getEncoding()+ "  "+ format.getSampleSizeInBits());
        //AudioFormat.Encoding encoding =  new AudioFormat.Encoding("PCM_SIGNED");
        //format = new AudioFormat(encoding,((float) 8000.0), 16, 1, 2, ((float) 8000.0) ,false);
        System.out.println(format.toString());


        if(! this.local) {
            // To time the output correctly, we also play at the input:
            auline = null;
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            try {
                auline = (SourceDataLine) AudioSystem.getLine(info);
                auline.open(format);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (auline.isControlSupported(FloatControl.Type.PAN)) {
                FloatControl pan = (FloatControl) auline
                        .getControl(FloatControl.Type.PAN);
                if (this.curPosition == Position.RIGHT)
                    pan.setValue(1.0f);
                else if (this.curPosition == Position.LEFT)
                    pan.setValue(-1.0f);
            }


            auline.start();
        }

        int nBytesRead = 0;
        byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
        long start = System.currentTimeMillis();

        //BooleanControl mute = (BooleanControl) auline.getControl(BooleanControl.Type.MUTE);
        //mute.setValue(true);

        try {
            while (nBytesRead != -1 && pktCount < 2000) {
                nBytesRead = audioInputStream.read(abData, 0, abData.length);

                if (nBytesRead >= 0) {
                    rtpSession.sendData(abData);

                    auline.write(abData, 0, abData.length);
                    pktCount++;
                }
                //System.out.println(pktCount);
                if(pktCount == 300) {
                    Enumeration<Participant> iter = this.rtpSession.getParticipants();
                    //System.out.println("iter " + iter.hasMoreElements());
                    Participant p = null;

                    while(iter.hasMoreElements()) {
                        p = iter.nextElement();

                        String name = "name";
                        byte[] nameBytes = name.getBytes();
                        String data= "abcd";
                        byte[] dataBytes = data.getBytes();


                        int ret = rtpSession.sendRTCPAppPacket(p.getSSRC(), 0, nameBytes, dataBytes);
                        System.out.println("!!!!!!!!!!!! ADDED APPLICATION SPECIFIC " + ret);
                        continue;
                    }
                    if(p == null)
                        System.out.println("No participant with SSRC available :(");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Time: " + (System.currentTimeMillis() - start)/1000 + " s");

        try { Thread.sleep(200);} catch(Exception e) {}

        this.rtpSession.endSession();

        try { Thread.sleep(2000);} catch(Exception e) {
            System.out.println("Exiting thread");

        }
        if(RTPSession.rtpDebugLevel > 1) {
            System.out.println("<- Run()");
        }
    }

}

