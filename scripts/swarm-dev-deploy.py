#! /usr/bin/python
#
# This script:
# - (Optional) Restarts bitnami to avoid heap space problems
# - Undeploys the current WAR file on pos-gateway-dev
# - Deploys the one set as an argument
# - Checks that the gateway was successfully launched in the test environment

import argparse
import os
import swarm

# Read command line arguments
parser = argparse.ArgumentParser(description='Deploy war to the development environment')

parser.add_argument('--war', help='WAR file to be deployed', required=True)
parser.add_argument('--username', default='manager-script', help='manager username')
parser.add_argument('--password', default='bitnami', help='manager password')
parser.add_argument('--server', default='pos-gateway-dev.swarm-mobile.com', help='server location')
parser.add_argument('--yes', default=False, action="store_true", help='always say yes whenever prompted to confirm')
parser.add_argument('--verify-tag', default=False, action="store_true", help='verify git tag')
parser.add_argument('--dir', default='.', help='repository location')
parser.add_argument('--ssl', default=False, action="store_true", help='Use SSL')

args = parser.parse_args()

# Verify that release was requested
msg = swarm.getLastCommitMessage(args.dir)
if args.verify_tag and not "#test" in msg and "[maven-release-plugin] prepare release" not in msg :
        print "Commit message doesn't contain #test, aborting release"
        raise SystemExit

# Domain
domain=args.server

# Use SSL?
protocol="http://"
if args.ssl:
        protocol="https://"

# Verify that file exists
if not os.path.isfile(args.war):
        raise Exception("War file not found" + args.war)
        
# Query for currently running version,
# and prompt user to verify removal
version=swarm.getVersion(protocol,domain)   
if version != False:

        # Prompt to verify removal
        if not args.yes:
                print "Current deployed version is: " + version['interfaceVersion']
                print "Are you sure you want to undeploy this instance? [Y/n]"       
                answer = raw_input()
                if answer != 'Y':
                        raise Exception("Aborted")

# Deploy new WAR
swarm.deployWar(protocol, domain, args.username, args.password, args.war)

# Access version and print to user
version = swarm.getVersion(protocol, domain)
if version == False:
        raise Exception("Failed to deploy WAR file to " + domain)

print "Swarm Gateway successfully started at " + domain + ", current version is: " + version['interfaceVersion'] + " built on: " + version['buildTimestamp']


