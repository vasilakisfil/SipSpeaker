package server;

//fix imports
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.*;
import org.apache.commons.cli.*;

public class SIPServer {
  static final Logger logger = LogManager.getLogger(SIPServer.class.getName());

  public static void main(String[] args) throws UnknownHostException, SocketException, IOException, InterruptedException {
    CommandLineParser parser = new GnuParser();
    CommandLine line;

    Options options = parseArguments(args);

  // create the parser
    try {
        // parse the command line arguments
        line = parser.parse( options, args );
        Configuration.sipUser(line.getOptionValue("sipUser"));
        Configuration.sipInterface(line.getOptionValue("sipIp"));
        Configuration.sipPort(line.getOptionValue("sipPort"));
    }
    catch( ParseException exp ) {
        // oops, something went wrong
        System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
    }

    System.out.println(Configuration.sipFullAddress());

    logger.info("Sip Server " + Configuration.sipUser()
    		+ " listening to :" + Configuration.sipInterface() + "- on port :" + Configuration.sipPort());
    SIPWorker sipWorker = new SIPWorker();
    sipWorker.start();
  }


  private static Options parseArguments(String[] args) {
    Options options = new Options();

    Option sipUser = OptionBuilder
        .withArgName("username").hasArg()
        .withDescription(  "sip username" )
        .create( "sipUser" );

    System.out.println(sipUser);

    Option sipIp = OptionBuilder
        .withArgName("IP").hasArg()
        .withDescription(  "ip for sip server" )
        .create( "sipIp" );

    Option sipPort = OptionBuilder
        .withArgName("port").hasArg()
        .withDescription(  "port for sip" )
        .create( "sipPort" );

    options.addOption(sipUser);
    options.addOption(sipIp);
    options.addOption(sipPort);

    return options;
  }

}
