module TTSServer
  # This class handles the HTTP requests
  # It follows the HTTP protocol naming
  class Request
    attr_reader :method, :request_uri, :http_version, :header_fields, :data

    # Initializes the request
    #
    # @param request [String] The request string
    def initialize(request)
      @logger = ::Logger.new(STDOUT)
      @logger.level = Logger::DEBUG
      @full_request = request
      @valid = false
      method, request_uri, http_version = request.lines.first.split " "
      header_fields = request.split("\n")[1..-1]
      if method == "GET" || method == "POST"
        @method, @request_uri, @http_version, @valid = method, request_uri,
                                                      http_version, true
      end
      @logger.debug { "Request line  #{@method} #{@request_uri} #{@http_version}" }

      @header_fields = parse_header_fields(header_fields)
      #logger.debug { "Request headers  #{@method} #{@request_uri} #{@http_version}" }
    end

    def parse_data(data)
      if @data
        @data
      else
        @data = parse_multipart(data)
      end
    end

    # @return [boolean] Boolean that indicates whether the request has a valid
    # structure
    def valid?
      @valid
    end

    def to_s
      "#{@full_request}#{@data}"
    end

    private

    # Method that parses all the HTTP header fields
    #
    # @params [Hash]
    def parse_header_fields(fields)
      header_fields = Hash.new
      fields.each do |line|
        case line
        when /^Accept:\s/
          header_fields[:Accept] = line.split(/^Accept:\s/)[1]
        when /^Accept-Charset:\s/
          header_fields[:Accept_Charset] = line.split(/^Accept-Charset:\s/)[1]
        when /^Accept-Encoding:\s/
          header_fields[:Accept_Encoding] = line.split(/^Accept-Encoding:\s/)[1]
        when /^Accept-Language:\s/
          header_fields[:Accept_Language] = line.split(/^Accept-Language:\s/)[1]
        when /^Accept-Datetime:\s/
          header_fields[:Accept_Datetime] = line.split(/^Accept-Datetime:\s/)[1]
        when /^Authorization:\s/
          header_fields[:Authorization] = line.split(/^Authorization:\s/)[1]
        when /^Cache-Control:\s/
          header_fields[:Cache_Control] = line.split(/^Cache-Control:\s/)[1]
        when /^Connection:\s/
          header_fields[:Connection] = line.split(/^Connection\s/)[1]
        when /^Cookie:\s/
          header_fields[:Cookie] = line.split(/^Cookie\s/)[1]
        when /^Content-Length:\s/
          header_fields[:Content_Length] = line.split(/^Content-Length:\s/)[1]
        when /^Content-MD5:\s/
          header_fields[:Content_MD5] = line.split(/^Content-MD5:\s/)[1]
        when /^Content-Type:\s/
          header_fields[:Content_Type] = line.split(/^Content-Type:\s/)[1]
        when /^Date:\s/
          header_fields[:Date] = line.split(/^Date:\s/)[1]
        when /^Expect:\s/
          header_fields[:Expect] = line.split(/^Expect:\s/)[1]
        when /^From:\s/
          header_fields[:From] = line.split(/^From:\s/)[1]
        when /^Host:\s/
          header_fields[:Host] = line.split(/^Host:\s/)[1]
        when /^If-Match:\s/
          header_fields[:If_Match] = line.split(/^If-Match:\s/)[1]
        when /^If-Modified-Since:\s/
          header_fields[:If_Modified_Since] = line.split(/^If-Modified-Since:\s/)[1]
        when /^If-None-Match:\s/
          header_fields[:If_None_Match] = line.split(/^If-None-Match:\s/)[1]
        when /^If-Range:\s/
          header_fields[:If_Range] = line.split(/^If-Range:\s/)[1]
        when /^If-Unmodified-Since:\s/
          header_fields[:If_Unmodified_Since] = line.split(/^If-Unmodified-Since:\s/)[1]
        when /^Max-Forwards:\s/
          header_fields[:Max_Forwards] = line.split(/^Max-Forwards:\s/)[1]
        when /^Origin:\s/
          header_fields[:Origin] = line.split(/^Origin:\s/)[1]
        when /^Pragma:\s/
          header_fields[:Pragma] = line.split(/^Pragma:\s/)[1]
        when /^Proxy-Authorization:\s/
          header_fields[:Proxy_Authorization] = line.split(/^Proxy-Authorization:\s/)[1]
        when /^Range:\s/
          header_fields = line.split(/^Range:\s/)[1]
        # officialy mispelled
        when /^Referer:\s/
          header_fields[:Referer] = line.split(/^Referer:\s/)[1]
        when /^TE:\s/
          header_fields[:TE] = line.split(/^TE\s/)[1]
        when /^Upgrade\s/
          header_fields[:Upgrade] = line.split(/^Upgrade:\s/)[1]
        when /^User-Agent:\s/
          header_fields[:User_Agent] = line.split(/^User-Agent:\s/)[1]
        when /^Via:\s/
          header_fields[:Via] = line.split(/^Via:\s/)[1]
        when /^Warning:\s/
          header_fields[:Warning] = line.split(/^Warning:\s/)[1]
        end
      end

      return header_fields
    end

    def parse_multipart(data)
      @logger.debug { "Parsing Request multipart data" }
      @logger.debug { data }
      array = data.split("\n")
      #fix that according to browser..
      random_indices = array.each_index.select{|i| array[i].include? "------"}
      values = {}
      return values if random_indices == [0]
      random_indices.each_index do |index|
        description = array[random_indices[index]+1]
        name_index = array[random_indices[index]+1].index("name=")
        name = description[name_index..description.length].gsub("name=","").gsub("\"","")

        values[name] = array[random_indices[index]+3...random_indices[index+1]]
        break if index == random_indices.length-2
      end
      #strip \r from values
      hash_values = {}
      values.each do |key, value|
        hash_values[key.gsub("\r","")] = value
      end
      hash_values["message"] = [hash_values["message"].join("\n")] if hash_values["message"]
      hash_values.each do |key, value|
        hash_values[key] = value.first.gsub("\r","")
      end
      @logger.debug { "Data parsed:" }
      @logger.debug { hash_values }
      return hash_values
    end



  end
end
