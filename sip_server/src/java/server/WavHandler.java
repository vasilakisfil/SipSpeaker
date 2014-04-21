package server;

import java.io.File;
import javax.media.*;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

 
import javax.sound.sampled.AudioFileFormat;

public class WavHandler extends Thread {

  public void SendWavFile(String wavFileName, String senderIpAddress, String RtpPort) throws Exception {
    System.out.println("--->" + wavFileName);
    MediaLocator locator = new MediaLocator("rtp://" + senderIpAddress + ":" + RtpPort + "/audio");
    File mediaFile = new File(wavFileName);
    DataSource source = Manager.createDataSource(new MediaLocator(mediaFile.toURL()));
    System.out.println("Sending from data source: '" + mediaFile.getAbsolutePath() + "'");

    /*The processor is responsible for reading the file from a file and converting it to
     * an RTP stream.
     * The processor used to read the media from a local file, and produce an
     * output stream which will be handed to the data sink object for broadcast.
     */
    Processor mediaProcessor = null;
    Format[] FORMATS = new Format[]{new AudioFormat(AudioFormat.GSM_RTP,8000,8,1)};
    ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(ContentDescriptor.RAW_RTP);
    mediaProcessor = Manager.createRealizedProcessor(new ProcessorModel(source, FORMATS, CONTENT_DESCRIPTOR));
    DataSink dataSink = null;
    /* Create the data sink.  The data sink is used to do the actual work 
    of broadcasting the RTP data over a network.
     */

    dataSink = Manager.createDataSink(mediaProcessor.getDataOutput(), locator);
    // start transmitting the file over the network.
    // start the processor
    mediaProcessor.start();

    // open and start the data sink
    dataSink.open();
    dataSink.start();
    double duration = mediaProcessor.getDuration().getSeconds();
    System.out.println("Duration of wav file: " + duration + "\r\n");

    //wait until the file is transmitted
    Thread.sleep(500 + 1000 * (int) duration);

    dataSink.stop();
    dataSink.close();
    mediaProcessor.stop();
    mediaProcessor.close();
  }
}
