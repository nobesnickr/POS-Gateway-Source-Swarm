#Add a new user called glassfish
sudo adduser --home /home/glassfish --system --shell /bin/bash glassfish

#add a new group for glassfish administration
sudo groupadd glassfishadm

#add your users that shall be Glassfish adminstrators
sudo usermod -a -G glassfishadm vagrant
sudo usermod -a -G glassfishadm glassfish