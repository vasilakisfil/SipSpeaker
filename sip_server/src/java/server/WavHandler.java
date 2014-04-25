package server;

import java.io.File;

import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.org.apache.xerces.internal.util.URI;

public class WavHandler extends Thread {
  static final Logger logger = LogManager.getLogger(WavHandler.class.getName());


  public void SendWavFile(String wavFileName, String senderIpAddress, String RtpPort) throws Exception {
    logger.info("Sending file" + wavFileName);
    String locatorUri = "rtp://" + senderIpAddress + ":" + RtpPort + "/audio";
    System.out.println("------------> " + locatorUri);
    MediaLocator locator = new MediaLocator(locatorUri);
    File mediaFile = new File(wavFileName);
    DataSource source = Manager.createDataSource(new MediaLocator((mediaFile.toURL())));
    System.out.println("------------> " + mediaFile.toURL());

    Processor mediaProcessor = null;
    Format[] FORMATS = new Format[]{new AudioFormat(AudioFormat.ULAW_RTP, 8000, 8, 1)};
    ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(ContentDescriptor.RAW_RTP);
    mediaProcessor = Manager.createRealizedProcessor(new ProcessorModel(source, FORMATS, CONTENT_DESCRIPTOR));
    DataSink dataSink = null;

    dataSink = Manager.createDataSink(mediaProcessor.getDataOutput(), locator);


    dataSink.open();
    dataSink.start();
    mediaProcessor.start();

    double duration = mediaProcessor.getDuration().getSeconds();
    System.out.println("Duration of wav file: " + duration + "\r\n");

    Thread.sleep(500 + 1000 * (int) duration);

    dataSink.stop();
    dataSink.close();
    mediaProcessor.stop();
    mediaProcessor.close();
  }
}
