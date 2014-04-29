require 'socket'
require 'parseconfig'
require 'logger'
require 'thread'

require_relative 'tts_server/configuration'
require_relative 'tts_server/answer_worker'
require_relative 'tts_server/request'
require_relative 'tts_server/response'
require_relative 'tts_server/tts'

module TTSServer
  # Starts and initiates the HTTP server
  class HTTPServer
    attr_reader :config_path, :server_root, :ip, :port

    # Initializes the HTTP server
    #
    # @param port [Integer] The port the server listens to
    # @param server_root [String] The directory the server points to
    def initialize(ip, port, default_message)
      @logger = Logger.new(STDOUT)
      @logger.level = Logger::DEBUG
      @port = port
      @ip = ip
      #initialize message to nil
      TTSServer.default_message = default_message
      #save default message to default.wav
      TTS.new(TTSServer.default_message).save(TTSServer::DEFAULT_MESSAGE)
      TTSServer.message = default_message
      TTS.new(TTSServer.message).save(TTSServer.message_path)
    end

    # Starts the server ready to accept new connections
    def start
      @logger.debug { "Opening server" }
      @tcp_server = TCPServer.new(@ip, @port)
      @logger.debug { "Listening to #{@ip} port #{@port}
                      pointing #{ROOT_DIR}" }

      answer_worker = AnswerWorker.new
      client = nil
      loop do
        begin
          client = @tcp_server.accept
          @logger.debug { "Server accepted new request" }
          answer_worker.start(client)
        rescue Interrupt => e
          @logger.debug { "Closing program" }
          exit
        rescue Exception => e
          @logger.debug { "Exception caught:" }
          @logger.debug { e }
        end
      end
      stop
    end

    # Close server
    def stop
      @tcp_server.close
      @logger.debug { "Server Closed" }
    end
  end
end
