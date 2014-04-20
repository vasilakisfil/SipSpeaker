require 'optparse'
require 'parseconfig'
require_relative 'web_server/lib/tts_server'


puts "Parsing options...:"
cli_options = TTSServer.parse_arguments
options = TTSServer.parse_configfile(cli_options["config_file"]).merge(cli_options)
puts "End configuration:"
puts options

puts "Starting web server"
Thread.new {
  TTSServer::HTTPServer.new(5555).start
}

puts "Starting sip server"
cmd = "java -jar build/libs/sip_web_server-1.0.jar -user robot@192.168.0.112:5666"
value = %x( #{cmd} )
puts value

