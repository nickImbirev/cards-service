# Cards service

The main idea of the application is to manage the daily routine tasks (cards)
and help to complete things in time according to the actual priorities.

## Getting started

### Prerequisites

- [JDK 11](https://jdk.java.net/java-se-ri/11) (or higher)
- [Gradle 7](https://gradle.org/releases/) (or higher)

### How to run service locally

- Clone this repository locally and open the project main folder.
- Go into the backend folder: `cd server`
- Install local Gradle dependencies: `gradle clean build`.
- Run project locally: `gradle run`.

_Note: the application server should get started on the default port: 8081.
Please, follow the server logs in the console to access the application._

### How to work with open service API

- Run the service locally or identify the connection details of the remote server.
- Open the open api documentation page on the following url: `/swagger-ui/`
  (example for the local server: `http://localhost:8081/swagger-ui/`).

_Note: service uses [Swagger](https://swagger.io/) as a primary API documentation tool.
You can see the available endpoints, read their descriptions and requirements,
also you can try them out on the running server._

### How to configure service

_Note: All the service configuration is available only during the service startup and cannot be changed without a restart._

In order to configure the service you can use the standard run command with an additional 'args' argument:
`gradle run --args=''`, where the configuration arguments can be provided with a space (` `) symbol as a delimiter.

There is a possibility to configure the following service values:

- maximum number of cards available for one day (first argument in the list)
  - can be any integer starts from 1
- day cards preparation schedule
  - the schedule period can be any integer starts from 1 (second argument in the list)
  - the time unit can be any value from the range: (DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS) (third argument in the list)

Example: `gradle run --args='5 3 MINUTES'` (maximum number cards per day - 5, sync schedule: every 3 minutes).

#### How to configure the service logging

_Note: There are [multiple](https://sematext.com/blog/logging-levels/) logging levels available._

_Note: The service logging values can only be configured in the [build.gradle application section](./build.gradle) only right now._

There is a possibility to configure the following service logging values:

- logging level: 
  - can be error, warn, info (default), debug with an argument: ``-Dorg.slf4j.simpleLogger.defaultLogLevel=YOUR_LEVEL``.

