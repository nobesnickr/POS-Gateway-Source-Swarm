#! /usr/bin/python
#
# This script:
# - Downloads the currently deployed WAR file from the test environment
# - Creates backup of the pos-production database
# - Undeploys the WAR file in the production environment
# - Deploys the one from the test environment
# - Verifies that gateway was successfully launched

import argparse
import swarm
import os

parser = argparse.ArgumentParser(description='release new version of the production environment')
parser.add_argument('--username', default='manager-script', help='manager username')
parser.add_argument('--dev-password', default='bitnami', help='manager password on pos-gateway-dev')
parser.add_argument('--prod-password', required=True, help='manager username on pos-gateway')
parser.add_argument('--dev-server', default='pos-gateway-dev.swarm-mobile.com', help='Test server location, source')
parser.add_argument('--prod-server', default='pos-gateway-dev.swarm-mobile.com', help='Production server location, destination')
parser.add_argument('--key', default='~/.ssh/swarm-tomcat.pem', help='Location of the SSH private key')
parser.add_argument('--ssl', default=False, action="store_true", help='Use SSL')
parser.add_argument('--allow-snapshots', default=False, action="store_true", help='Allow releasing snapshots')

args = parser.parse_args()

# Check which version is running in the DEV enviroment
# Use SSL?
protocol="http://"
if args.ssl:
        protocol="https://"
        
# Access version of the prod gateway and print to user
version = swarm.getVersion(protocol, args.prod_server)
if version == False:
        print "No gateway is running in the production environment"
else:
        print version['interfaceVersion'] + " is running on the production environment" 
        
# Access dev version and print to user
version = swarm.getVersion(protocol, args.dev_server)
if version == False:
        raise Exception("Gateway is not running at " + args.dev_server)
if 'SNAPSHOT' in version['interfaceVersion'] and not args.allow_snapshots: 
        raise Exception("Snapshots can't be released to the production: " + version['interfaceVersion'])
        
print version['interfaceVersion'] + " is running on the development environment"
        
print "Are you sure you want proceed with the release? [Y/n]"       
answer = raw_input()
if answer != 'Y':
        raise SystemExit
        
# Name of the file to be saved as
filename = 'swarm-dev.war'

# Download WAR file from the production environment
os.system("scp -i " + args.key + " bitnami@" + args.dev_server + ":/opt/bitnami/apache-tomcat/webapps/swarm.war " + filename)

# Deploy WAR to production
swarm.deployWar(protocol, args.prod_server, args.username, args.prod_password, filename)

# Access version and print to user
version = swarm.getVersion(protocol, args.prod_server)
if version == False:
        raise Exception("Failed to deploy WAR file to " + domain)

print "Swarm Gateway successfully started at " + args.prod_password + ", current version is: " + version['interfaceVersion'] + " built on: " + version['buildTimestamp']

# Remove WAR file
os.system("rm " + filename)



