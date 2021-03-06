module TTSServer
  ROOT_DIR = "#{File.expand_path(File.dirname(__FILE__))}/../assets/"
  ERROR_404_PAGE = "#{ROOT_DIR}/404.html"
  DEFAULT_PAGE = "#{ROOT_DIR}/default.html"
  DEFAULT_MESSAGE = "#{ROOT_DIR}../../../default.wav"
  CURRENT_FILE_MESSAGE = "#{ROOT_DIR}../../../currentmessage.wav"
  DEFAULT_MESSAGE_MP3 = "#{ROOT_DIR}../../../default.mp3"
  CURRENT_FILE_MESSAGE_MP3 = "#{ROOT_DIR}../../../currentmessage.mp3"
  @@message = nil

  def self.message=(message)
    @@message = message
  end

  def self.message
    if @@message
      return @@message
    else
      return @@default_message
    end
  end

  def self.default_message=(message)
    @@default_message = message
  end

  def self.default_message
    @@default_message
  end

  def self.delete_message
    @@message = nil
  end

  def self.message_path
    return CURRENT_FILE_MESSAGE
  end

  def self.defaults
    options = {}
    # The default message wave-file. If the user chooses to delete the current message a
    # default should exist. If the file wav-file specified here for the default doesn't
    # exist or if the configuration file doesn't exist a default file should be created
    # on startup
    options["default_message"] = "default.wav"
    # Current message (message_wav and message_text)
    # The current message is the message that was set by the web configuration interface
    # and should be saved when the application exits. If the current message is deleted the default
    # should be played.
    options["message_wav"] = "currentmessage.wav"
    options["message_text"] = "Welcome to SIP Speaker. This is my own answering machine. You have no new messages."
    # Below is the network interface address that the sip server should bind and listen to. To listen to all
    # existing interfaces, use address 0.0.0.0. (This is true for the web server as well.) 
    options["sip_interface"] = "0.0.0.0"
    options["sip_port"] = 5060
    options["sip_user"] = "robot"
    # HTTP web server
    options["http_interface"] = "127.0.0.1"
    options["http_port"] = 5555

    return options
  end

  def self.parse_configfile(filename)
    options = self.defaults
    if filename
      if File.exists? filename
        config = ParseConfig.new(filename)
      else
        puts "Configuration File not found"
        exit
      end
    else
      config = ParseConfig.new('sipspeaker.cfg')
    end

    if !config["default_message"].nil? && !config["default_message"].empty?
      options["default_message"] = config["default_message"]
    end
    if !config["message_wav"].nil? && !config["message_wav"].empty?
      options["message_wav"] = config["message_wav"]
    end
    if !config["message_text"].nil? && !config["message_text"].empty?
      options["message_text"] = config["message_text"]
    end
    if !config["message_received"].nil? && !config["message_received"].empty?
      options["message_received"] = config["message_received"]
    end
    if !config["sip_interface"].nil? && !config["sip_interface"].empty?
      options["sip_interface"] = config["sip_interface"]
    end
    if !config["sip_port"].nil? && !config["sip_port"].empty?
      options["sip_port"] = config["sip_port"]
    end
    if !config["sip_user"].nil? && !config["sip_user"].empty?
      options["sip_user"] = config["sip_user"]
    end
    if !config["http_interface"].nil? && !config["http_interface"].empty?
      options["http_interface"] = config["http_interface"]
    end
    if !config["http_port"].nil? && !config["http_port"].empty?
      options["http_port"] = config["http_port"]
    end

    options["sip_uri"] = "#{options["sip_user"]}@#{options["sip_interface"]}:#{options["sip_port"]}"
    options["http_bind_address"] = "#{options["http_interface"]}:#{options["http_port"]}"

    return options
  end

  #fix arguments unix style
  def self.parse_arguments
    options = {}
    args = OptionParser.new do |opts|
      opts.on("-c", "--config_file file", "Specify configure file") do |v|
        options["config_file"] = v
      end

      opts.on("-u", "--user user", "Specify user sip address") do |v|
        options["sip_uri"] = v
      end

      opts.on("-h", "--http http", "Specify web server address") do |v|
        options["http"] = v
      end
    end

    begin
      args.parse!
    rescue OptionParser::InvalidOption => e
      puts args.help()
      exit
    end

    if options["sip_uri"]
      options["sip_user"] = options["sip_uri"].split("@")[0]
      if options["sip_uri"].include? ":"
        options["sip_interface"] = options["sip_uri"].split("@")[1].split(":")[0]
        options["sip_port"] = options["sip_uri"].split(":")[1]
      else
        options["sip_interface"] = options["sip_uri"].split("@")[1]
        options["sip_port"] = "5666"
      end
    end

    if options["http"]
      if options["http"].include? ":"
        options["http_interface"] = options["http"].split(":")[0]
        options["http_port"] = options["http"].split(":")[1]
      else
        if options["http"].include? "."
          options["http_interface"] = options["http"]
          options["http_port"] = "8080"
        else
          options["http_interface"] = "0.0.0.0"
          options["http_port"] = options["http"]
        end
      end
    end

    return options
  end

end
