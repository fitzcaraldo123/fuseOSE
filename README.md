TMG Camel Framework
====================

This project will provide a bean that can handle Camel exchange body, header and property manipulation.

A small example is set on **gateway-example**.


Testing
--------------

For my testing I used a Node.js server to create an external HTTP server, the script is inside the **test-helper** folder.
To run it, just install **Node.js** in your machine and the run the command : node request_logger.js

For the message system I used **apache-activemq-5.14.1**. Every configuration file that was changed in activemq were put inside
the **gateway-example** project, into  **amq-cfg** folder. Just replace the env file into /bin folder and the rest into /conf folder.
