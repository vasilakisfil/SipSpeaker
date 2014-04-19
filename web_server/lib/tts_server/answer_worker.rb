require 'socket'
require 'logger'

module TTSServer
  # AnswerWorker handles each new request. Currently it handles only one request
  # per connection but in the future it will implement HTTP keep-alive too with multi-threading.
  class AnswerWorker
    attr_reader :request, :response, :server_root, :client

    # Initializes the AnswerWorker
    def initialize
      @logger = ::Logger.new(STDOUT)
      @logger.level = Logger::DEBUG
    end

    # Starts the AnswerWorker.
    #
    # @param client [tcpsocket] The tcp socket of the client
    # @param server_root [string] The directory that the server points to
    def start(client)
      request = read_request(client)
      @logger.debug { "Request analyzed, now reading data if any" }

      response = Response.new(request)
      @logger.debug { "Response created and is sent to client" }

      client.puts response.to_s
      client.close
    end

    private

    # Reads the HTTP request from a tcpsocket
    #
    # @param client [tcpsocket] The tcp socket from which the request will
    # be read
    # @return [Request] The Request object with the request sring
    def read_request(client)
      req = ""

      while line = client.gets
        req += line
        break if line =~ /^\s*$/
      end

      request = Request.new(req)
      if request.method == "POST"
        puts request.header_fields[:Content_Length].to_i
        request.parse_data(
          client.read(request.header_fields[:Content_Length].to_i)
        )
      end
      return request
    end

    # Creates a new respons according to the given request
    # @param request [String] The HTTP request string
    # @return [Response] The HTTP response as a string
    def create_response(request)
      response = Response.new
    end
  end
end
