module TTSServer
  ROOT_DIR = "#{File.expand_path(File.dirname(__FILE__))}/../assets/"
  ERROR_404_PAGE = "#{ROOT_DIR}/404.html"
  DEFAULT_PAGE = "#{ROOT_DIR}/default.html"
  DEFAULT_MESSAGE = "#{ROOT_DIR}../../../default.wav"
  DEFAULT_MESSAGE_STR = "This is the default message"
  CURRENT_FILE_MESSAGE = "#{ROOT_DIR}../../../currentmessage.wav"

  def self.message
    if @@message
      return @@message
    else
      return DEFAULT_MESSAGE_STR
    end
  end

  def self.message=(message)
    @@message = message
  end

  def self.delete_message
    @@message = nil
  end

  def self.message_path
    if @@message
      return CURRENT_FILE_MESSAGE
    else
      return DEFAULT_MESSAGE
    end
  end

end
