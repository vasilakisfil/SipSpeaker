This program is written in both Ruby and Java. In order to act as one program
first Ruby is called which subsequently calls Java.

Before trying to run the server, Ruby and Java (1.7) needs to be installed on the system.
We also use Gradle as automation and dependency resolver tool.
An easy way to achieve that is by using the RVM - Ruby Version Manager tool found at https://rvm.io/

Once Ruby is installed, in order to run the server the following commands can be used:

bundle install #installs necessary libs for Ruby
gradle build #install necessary libs for Java
gradle uberjar #create Java jar that Ruby calls

To run:
ruby start.rb [arguments]
