require 'optparse'
require 'parseconfig'
require 'open3'
require_relative 'web_server/lib/tts_server'


puts "Parsing options...:"
cli_options = TTSServer.parse_arguments
puts cli_options
options = TTSServer.parse_configfile(cli_options["config_file"]).merge(cli_options)
puts "End configuration:"
puts options

puts "Starting web server"
Thread.new {
  TTSServer::HTTPServer.new(options["http_interface"], options["http_port"], options["message_text"]).start
}

puts "Starting sip server"
cmd = "java -jar build/libs/SipSpeaker-1.0.jar \
      --sipUser #{options["sip_user"]} \
      --sipIp #{options["sip_interface"]} \
      --sipPort #{options["sip_port"]}"
#value = %x( #{cmd} )
puts cmd
Open3.popen3(cmd) do |stdin, stdout, stderr, wait_thr|
  pid = wait_thr[:pid]
  Thread.new {
    stdout.each do |l|
      puts l
    end
  }

  stderr.each do |l|
    puts l
  end
end

#puts value

