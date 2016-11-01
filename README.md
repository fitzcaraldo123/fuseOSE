TMG Camel Framework
====================

This project will provide a bean that can handle Camel exchange body, header and property manipulation.

A small example is set on **gateway-example**.


Testing
--------------

For my testing I used a Node.js server to create an external HTTP server, the script is inside the **test-helper** folder.
To run it, just install **Node.js** in your machine and then run the command : node request_logger.js

For the message system I used **apache-activemq-5.14.1**. Every configuration file that was changed in activemq were put inside
the **gateway-example** project, into  **amq-cfg** folder. Just replace the env file into /bin folder and the rest into /conf folder.
Also, add the properties file in the resources folder into Activemq conf/

Deploy the generated jar from **gateway-example** into lib/ folder in Activemq. The application will start with the broker.

1. Testing HeaderHandlerExampleRouteBuilder
  
    the idea on this route is to simulate various state changes in the message, so in the last destination we have all the body changes
    in the last message. 
    
    [ActiveMQ Console](http://localhost:8161/admin/) or [Hawtio](http://localhost:8161/hawtio/) to send a message to FirstQueue.
    By the end of the process you should have a message in the fourthResponse queue. 
    
    If you want to test how try catch handles validation exceptions, just change one of the external URL from cfg to "/errorResponse".
    This way the node server will send an invalid message body.

2. Testing XpathRouteBuilder

    This route simulates the state change in the message given a xpath filter on a XML body. In the end of this route you
    shall have a message in fifthResponse queue with the proper state header set with the xpath query result as value.

    [ActiveMQ Console](http://localhost:8161/admin/) or [Hawtio](http://localhost:8161/hawtio/) to send a message to fifthQueue.
    By the end of the process the fifthResponse queue will have the result of the xpath query '//ord:product' over node's 
    http:localhost:9090/secondRequest response.