# CROSS-SERVER PROJECT

## Description

The **CROSS SERVER PROJECT** project is demo project for JBoss6 to Wildfly9 remote communication 

### Prerequisites

#### server client libraries on local machine 

##### from Wildfly distribution
bin\client\jboss-client.jar

##### from JBoss6 distribution
client\jbossall-client.jar and all libraries in client folder

#### cross-server-test/pom.xml
Two profiles *wildfly9* and *jboss6*, with parametrized location of client libs.

Change client.lib.location property to your location.

Create two Maven build configuration based on profiles.
*wildfly9* profile builds ear for Wildfly server, *jboss6* profile builds ear for JBoss6 profile

### Start servers on localhost

JBoss6 with default configuration, Wildfly9 with port offset 100

### Start demo

run JUnit SimpleTestCase in cross-server-ejb-jboss6
run JUnit SimpleTestCase in cross-server-ejb-wildfly9

### Owners

Centili Team @ Infobip Belgrade, Serbia

 © 2014, Infobip Ltd.







