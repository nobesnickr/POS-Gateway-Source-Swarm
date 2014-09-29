POS-Gateway-Source
==================

This folder contains the POS gateway's source files. It uses Maven for build management. Should you wish to compile the WAR file of the gateway follow these steps:


### 1. Edit the **pom.xml** file in your the root folder

Replace parts with the appropriate repository and source control management configuration or delete them (recommended for one-time compiling). 

* If you install all the non-standard dependencies manually into your local Maven repository, you can delete the **distributionManagement** field, as all JAR files will be found either in your local repository or in the central Maven repository. 
* If you do not want to create releases of the gateway it's not necessary to set up source control management, therefore you can delete the **scm** field.

### 2. Install non-standard dependencies

Currently this is only the *sonrisa-backend.jar* which I placed in the **lib folder** for convenience. Google *"install maven dependency manually"* for guides on how the install the jar file. 

### 3. Build a snapshot

    mvn clean install
    
This will create a snapshot of the gateway to **swarm-gateway/target/swarm.war**. 

Snapshots must never be deployed to the production environment, use the **swarm-dev-deploy.py** to release a new version to the **pos-gateway-dev**, and then use the **swarm-production-deploy.py** on the production machine to release the new version verified in the development environment.