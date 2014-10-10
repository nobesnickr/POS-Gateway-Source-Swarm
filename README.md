POS-Gateway-Source
==================

This folder contains the POS gateway's source files. It uses Maven for build management. 

### How to build the gateway?

Firstly you must install **sonrisa-backend** in your local Maven repository, as it's not available in the central repository:

    cd sonrisa-backend
    mvn clean install
    cd ..
    
Afterwards, you have to build the gateway itself:

    mvn clean install
    
This will create a snapshot of the gateway at **swarm-gateway/target/swarm.war**. 

Snapshots must never be deployed to the production environment, use the **swarm-dev-deploy.py** to release a new version to the **pos-gateway-dev**, and then use the **swarm-production-deploy.py** on the production machine to release the new version verified in the development environment.

### How to prepare the environment for running the gateway?

The gateway depends on two databases, one for POS data and with the *exp_members* table for authentication. These databases have to be created prior to launching the gateway. Suggested engine to be used is **MySQL**.

The database for the **POS data** can be created empty in which case the tables are automatically generated. Alternatively they can be a clone of the production environment (pos_production) in which case the production data is kept. For demo purposes create a database with name "swarm" and grant access for username/password swarm/swarm, as these are the default values set up.

The database with the **expression engine tables** has to have two tables: exp_members and exp_member_groups. Use the script located at: *swarm-gateway/src/test/resources/eeuser-generate.sql*, to create dummy content. Using this script two users are created: sonrisa and admin, both with password "sonrisa2013". The admin has more priviliges.

Finally you have to place a **swarm.properties** file in you home directory. You have to enter the AES encryption key used for encryption on the stores table in this file like so:

    encryption.aes.key=YOURKEYFORENCRPYTINGBLOBSINTHESTORESTABLE
    
If your stores table is empty, this can be any value you choose, as new entities will be encrypted using this value.

### How to launch the gateway with Jetty?

For demoing purposes you can launch the gateway with **Jetty**. Firstly make sure that the databases are running and they can be accessed with the values defined in this file: *swarm-gateway/src/main/webapp/WEB-INF/jetty-env.xml.*

Then run this:

    mvn jetty:run-war
    
You can verify that the gateway launched successfully by going to this URI: http://localhost:8080/swarm/api/version

### How to launch the gateway with Tomcat?

If you wish to deploy it to **Tomcat**, you must set up Tomcat's context configuration accordingly to the database access values. Example:

    <?xml version='1.0' encoding='utf-8'?>
    <Context>
    ...
    <Resource name="jdbc/swarm" auth="Container" type="javax.sql.DataSource"
     maxActive="100" maxIdle="30" maxWait="10000" username="swarm"
     password="swarm" driverClassName="com.mysql.jdbc.Driver"
     url="jdbc:mysql://localhost:3306/swarm?zeroDateTimeBehavior=convertToNull"
    />
    <Resource name="jdbc/eeuser" auth="Container" type="javax.sql.DataSource"
     maxActive="100" maxIdle="30" maxWait="10000" username="eeuser"
     password="eeuser" driverClassName="com.mysql.jdbc.Driver"
     validationQuery="SELECT 1" testOnBorrow="true"
     url="jdbc:mysql://localhost:3306/eeuser?autoReconnect=true"
    />
    </Context>
    
Then go to Tomcat manager (usually located at /manager) and upload the WAR file. 