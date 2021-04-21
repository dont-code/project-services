![image](https://dont-code.net/assets/logo-shadow-squared.png)
## What is it for ?

These services manage the dont-code projects: Saving, Loading, Searching...
They are part of the [dont-code](https://dont-code.net) no-code / low-code platform enabling you to quickly produce your very own application.

## What is it ?
These services are developed in [Quarkus](https://quarkus.io) and uses a [Mongo](https://mongodb.com) database to store the project information.

## How is it working ?
Project services are just Rest Services using Quarkus RestEasy Reactive and Mongo Reactive

## How to build it ?
This project is a standard maven project:

1. Installing

   Download and Install [Maven](https://maven.org) if necessary.

   Download and Install a local [Mongo database](https://mongodb.com) if you want to run tests
   
   - You can define environment variable `_TEST_QUARKUS_MONGODB_PROJECTS_CONNECTION_STRING` to an existing mongodb if you want to override the default 'mongodb://localhost:27017' connection url during the test.
   - You can define environment variable `_DEV_QUARKUS_MONGODB_PROJECTS_CONNECTION_STRING` to an existing mongodb if you want to override the default 'mongodb://localhost:27017' connection url during the development.

2. Running tests

   `mvn test`

3. Building

   `mvn package` to produce the Uber Jar project-services-runner.jar
   
4. Running in dev mode enabling lib coding

   `mvn quarkus:dev` to start the services

   `http://localhost:8083` to access the homepage

4. Running in production mode

   `java -jar target/project-services-runner.jar`

## Thank you

This project was generated using [Quarkus io generator](https://code.quarkus.io/).
