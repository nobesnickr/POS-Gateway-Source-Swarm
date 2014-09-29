#! /usr/bin/python
import argparse
import json
import requests
import time
import os
import subprocess
import swarm

def getLastCommitMessage(repository):
        proc = subprocess.Popen("git log -1 " + repository, stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()     
        return out        
        
# Read current project version from a Maven repository
def getMavenVersion(repository):
        proc = subprocess.Popen("mvn help:evaluate -Dexpression=project.version -f " + repository + " | sed -e 1h -e '2,3{H;g}' -e '/\[INFO\] BUILD SUCCESS/ q' -e '1,2d' -e '{N;D}' | sed -e '1q'", stdout=subprocess.PIPE, shell=True)
        (out, err) = proc.communicate()        
        return out

# Get the current version using the /api/version REST service
def getVersion(protocol, domain):
        url = protocol + domain + '/swarm/api/version'
        result = requests.get(url, verify=False)   
        
        if result.status_code == 200:
                return result.json()
        else:
                return False
                
                
# Restart bitnami at a given location
def restartBitnami(protocol, domain, username, password, key):
    
        # Undeploy war
        print "Removing /swarm from Tomcat"
        result = requests.get(protocol + domain + '/manager/text/undeploy?path=/swarm', auth=(username, password), verify=False)
        print result.text
        
        # Restart bitnami
        command = "ssh bitnami@" + domain + " -i " + key + ' "sudo service bitnami restart; echo RESTARTED; exit;" '
        print "Executing: " + command
        os.system(command)
        
        # http://localhost:8080/manager/text/list
        count = 0
        while count < 100:
                result = requests.get(protocol + domain + '/manager/text/list', auth=(username, password), verify=False)
                if result.status_code == 200:
                        print "Tomcat manager reports: " + result.text
                        break
                else: 
                        print "Failed to access Tomcat manager. Retrying in 30 seconds"
                        time.sleep(30)
                count = count+1

# Deploys WAR file to a given location
def deployWar(protocol, domain, username, password, war):
        print "Deploying new war file to " + domain

        files = {'swarm.war': open(war, 'rb')}
        result = requests.put(protocol + domain + '/manager/text/deploy?path=/swarm&update=true', files=files, auth=(username, password), verify=False)

        if result.status_code == 200:
                print "Tomcat says:" + result.text
        elif result.status_code == 500:
                print "Tomcat manager errored out, attempting access /api/version"
                count = 0
                while count < 100:
                        version=getVersion(protocol,domain)
                        if version != False:
                                break
                        else:
                                print "Failed to read version. Retrying in 30 seconds"
                                time.sleep(30)
                        count = count+1
                        
                         
        else:   
                print "Tomcat error:" + result.status_code
