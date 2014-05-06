SipSpeaker
=========

Simple answering machine written in both Ruby and Java. Ruby implements the web server through which the user can configure the answering machine options and Java implements the SIP serve along with SDP and RTP protocols.

Before trying to run the server, Ruby (with rvm) and Java (1.7) (with Gradle) need to be installed on the system.

In order to run the server:

```
bundle install #installs necessary libs for Ruby
gradle build #install necessary libs for Java
gradle uberjar #create Java jar that Ruby calls
```

To run:
```
ruby start.rb  [-c config_file_name] [-user sip_uri] [-http http_bind_address]
```
Contributors:  
[Vasileios Panopoulos](https://github.com/vassilisp)  
[Filippos Vasilakis](https://github.com/vasilakisfil)
