module TTSServer
  # Class that holds an HTTP response
  # Follows HTTP protocol naming
  class Response
    STATUS_CODE = { 100 => "Continue", 101 => "Switching Protocols",
    102 => "Processing", 200 => "OK", 201 => "Created", 202 => "Accepted",
    203 => "Non-Authoritative Information", 204 => "No Content",
    205 => "Reset Content", 206 => "Partial Content", 207 => "Multi-Status",
    208 => "IM Used", 300 => "Multiple Choices", 301 => "Moved Permanently",
    302 => "Found", 303 => "See Other", 304 => "Not Modified",
    305 => "Use Proxy", 306 => "Switch Proxy", 307 => "Temporary Redirect",
    308 => "Permanent Redirect", 400 => "Bad Request", 401 => "Unauthorized",
    402 => "Payment Required", 403 => "Forbidden", 404 => "Not Found",
    405 => "Method Not Allowed", 406 => "Not Acceptable",
    407 => "Proxy Authentication Required", 408 => "Request Timeout",
    409 => "Conflict", 410 => "Gone", 411 => "Length Required",
    412 => "Precondition Failed", 413 => "Request Entity Too Large",
    415 => "Unsupported Media Type", 416 => "Requested Range Not Satisfiable",
    417 => "Expectation Failed", 419 => "Authentication Timeout",
    420 => "Enhance Your Calm", 426 => "Upgrade Required",
    428 => "Precondition Required", 429 => "Too Many Requests",
    451 => "Unavailable For Legal Reasons", 500 => "Internal Server Error",
    501 => "Not Implemented", 502 => "Bad Gateway", 503 => "Service Unavailable",
    504 => "Gateway Timeout", 505 => "HTTP Version Not Supported" }

    attr_accessor :status_line, :http_version, :status_code, :header_field,
      :body

    # Initializes the most basic fields of the HTTP response
    # (Any field can be re-configured through header_field method)
    def initialize(request)
      @logger = ::Logger.new(STDOUT)
      @logger.level = Logger::DEBUG
      @request = request
      create_headers
      create_body
    end

    def create_headers
      current_time = Time.new.utc.strftime("%a, %d %b %Y %H:%M:%S")
      @http_version = "HTTP/1.1"
      @header_field = Hash.new
      @header_field[:'Access-Control-Origin'] = "*"
      @header_field[:'Accept-Ranges'] = "bytes"
      @header_field[:Age] = "0"
      @header_field[:Allow] = "GET"
      @header_field[:'Cache-Control'] = "private, max-age=0"
      @header_field[:Connection] = ""
      # fix that
      @header_field[:'Content-Encoding'] = ""
      @header_field[:'Content-Language'] = "en"
      # fix that
      @header_field[:'Content-Length'] = ""
      # fix that
      @header_field[:'Content-MD5'] = ""
      @header_field[:'Content-Disposition'] = ""
      @header_field[:'Content-Range'] = ""
      @header_field[:'Content-Type'] = "text/html; charset=utf-8"
      @header_field[:Date] = "#{current_time} GMT"
      @header_field[:ETag] = ""
      @header_field[:Expires] = "-1"
      @header_field[:'Last-Mmodified'] = "#{current_time} GMT"
      @header_field[:Link] = ""
      @header_field[:Location] = ""
      @header_field[:P3P] = ""
      @header_field[:Pragma] = ""
      @header_field[:'Proxy-Authenticate'] = ""
      @header_field[:Refresh] = ""
      @header_field[:'Retry-After'] = "60"
      @header_field[:Server] = "GoatServer 0.0001 (Unix)"
      @header_field[:'Set-Cookie'] = ""
      @header_field[:Status] = @status_code =  "200"
      @header_field[:'Strict-Transport-Security'] = ""
      @header_field[:Trailer] = ""
      @header_field[:'Transfer-Encoding'] = ""
      @header_field[:Vary] = ""
      @header_field[:Via] = ""
      @header_field[:Warning] = ""
      @header_field[:'WWW-Authenticate'] = ""
      @status_line = "#{@http_version} #{@status_code} #{STATUS_CODE[@status_code.to_i]}"
    end

    def create_body
      if @request.request_uri == "/index" && @request.method == "GET"
        create_index_body(TTSServer.message)
      elsif @request.request_uri == "/index" && @request.method == "POST"
        @logger.debug { "User submitted new message to be recorded" }
        TTSServer.message = @request.data["message"]
        TTS.new(TTSServer.message).save(TTSServer.message_path)
        create_index_body(TTSServer.message)
      else
        create_default_body
      end
    end

    def create_index_body(message)
      @logger.debug { "Creating index body with message: '#{message}'" }
      filepath = "#{TTSServer::ROOT_DIR}/index.html"
      self.body = HTTPBody.new(filepath).add_message!(message)
      self.header_field[:'Content-Type'] = "text/html; charset=utf-8"
    end

    def create_default_body
      @logger.debug { "Creating a default body" }
      filepath = "#{TTSServer::ROOT_DIR}#{@request.request_uri}"
      if File.exists? filepath
        case @request.request_uri
        when /\.(?:html)$/i
          self.body = HTTPBody.new(filepath)
          self.header_field[:'Content-Type'] = "text/html; charset=utf-8"
        when /\.(?:css)$/i
          self.body = HTTPBody.new(filepath)
          self.header_field[:'Content-Type'] = "text/css"
        when /\.(?:js)$/i
          self.body = HTTPBody.new(filepath)
          self.header_field[:'Content-Type'] = "application/javascript"
        when /\.(?:jpg)$/i
          file = File.open(filepath, "rb")
          self.body = file.read
          self.header_field[:'Accept-Ranges'] = "bytes"
          self.header_field[:'Content-Type'] = "image/jpeg"
          file.close
        when /\.(?:png)$/i
          file = File.open(filepath, "rb")
          self.body = file.read
          self.header_field[:'Accept-Ranges'] = "bytes"
          self.header_field[:'Content-Type'] = "image/png"
          file.close
        else
          self.body = HTTPBody.new(ERROR_404_PAGE)
          @header_field[:Status] = @status_code =  "404"
          @status_code = "404"
          @status_line = "#{@http_version} #{@status_code} #{STATUS_CODE[@status_code.to_i]}"
        end
      else
        self.body = HTTPBody.new(ERROR_404_PAGE)
        @header_field[:Status] = @status_code =  "404"
        @status_code = "404"
        @status_line = "#{@http_version} #{@status_code} #{STATUS_CODE[@status_code.to_i]}"
      end
    end

    def header_fields
      @header_fields = ""
      @header_field.each do |field, value|
        @header_fields += "#{field}: #{value}\n" if !value.empty?
      end
      return @header_fields
    end

    def to_s
      response = "#{@status_line}\n#{@header_fields}\n#{@body}"
    end

    private

    class HTTPBody
      # should fix these by providing abstract method
      MESSAGE_DIV = "<p id='message'> </p>"

      def initialize(filename=nil)
        @document = File.read(filename)
      end

      def add_message!(message)
        @document = @document.gsub(MESSAGE_DIV, "<p id='message'> #{message} </p>")
      end

      def to_s
        @document.to_s
      end
    end
  end
end
