# Simple AWS QUEUE Example - REST

This repo aim the use of JDK - AWS - Simple Queue made with Java - Spring Boot

# Maven Install
Create your self container server making first your jar artifact.

```shell
$ mvn install 
```

Then run the project as following:

```shell
$ java - jasr /target/app.jar 
```
Where app.jar is the name of the artifact indicated in the pom.xml.

# Consume simple services
You can consume this simple services to probe the SQS functionality:

* http://your-server:8080/health : **_Verify the service's health_**
* http://your-server:8080/create/{queue-name} : **_Create a new queue_**
* http://your-server:8080/send/{message-to-queue} : **_Send a message to the new queue_**
* http://your-server:8080/list/{queue-name} : **_List all queue messages_**
