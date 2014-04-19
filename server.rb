require_relative 'lib/tts_server'

TTSServer::HTTPServer.new(5555).start
