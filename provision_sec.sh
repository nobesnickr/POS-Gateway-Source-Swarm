#Add a new user called glassfish
#sudo adduser --home /home/glassfish --system --shell /bin/bash glassfish

#add a new group for glassfish administration
#sudo groupadd glassfishadm

#add your users that shall be Glassfish adminstrators
#sudo usermod -a -G glassfishadm vagrant
#sudo usermod -a -G glassfishadm glassfish



################################################################
#Installing Java
################################################################

#remove OpenJDK if installed
#sudo apt-get remove openjdk-6-jre openjdk-6-jdk
 
#install Sun JDK
#sudo apt-get install python-software-properties
#sudo add-apt-repository ppa:webupd8team/java
#sudo apt-get update

#sudo apt-get install oracle-java7-installer