# Cards service

The main idea of the application is to manage the daily routine tasks (cards) 
and help to complete things in time according to the actual priorities. 

## Getting started

### Prerequisites

- [JDK 11](https://jdk.java.net/java-se-ri/11) (or higher)
- [Gradle 7](https://gradle.org/releases/) (or higher)

### How to run service locally

- Clone this repository locally and open the project main folder.
- Install local Gradle dependencies: ``gradle clean build``.
- Run project locally: ``gradle run``.

*Note: the application server should get started on the default port: 8081. 
Please, follow the server logs in the console to access the application.*

### How to work with open service API

- Run the service locally or identify the connection details of the remote server.
- Open the open api documentation page on the following url: ``/swagger-ui/`` 
(example for the local server: ``http://localhost:8081/swagger-ui/``).

*Note: service uses [Swagger](https://swagger.io/) as a primary API documentation tool. 
You can see the available endpoints, read their descriptions and requirements, 
also you can try them out on the running server.*
