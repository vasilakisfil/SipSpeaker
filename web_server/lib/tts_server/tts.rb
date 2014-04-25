require 'espeak'

module TTSServer
  class TTS
    include ESpeak

    attr_reader :speach

    def initialize(text)
      @logger = ::Logger.new(STDOUT)
      @logger.level = Logger::DEBUG
      @logger.debug { "Creating new audio from text: #{text}" }
      @speech = Speech.new(text, voice: "en", pitch: 90, speed: 150)
    end

    def save(filename)
      @logger.debug { "Saving the audio to filename: #{filename}" }
      @speech.save("test.mp3")
      command = "sox test.mp3 -r 8000 -c 1 -b 8 #{filename}"
      ret_value = %x( #{command} )
      ret_value = %x( rm test.mp3 )
    end
  end
end
