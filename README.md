scratch-spring-rest
==============

A very simple webapp that can be used to quickly try out code within a Spring Boot project.

#### Build

To build this project you will first have to build and install the [`scratch-parent`](https://github.com/karlbennett/scratch-parent) to get access to the parent pom that is used to configure the project.

Then you will need to build and install the [`scratch-user-api`](https://github.com/karlbennett/scratch-user-api) project. This contains the common user API that all the `scratch-*-rest` projects implement.

Once you have built and installed the above you will be able to build this project with:

    mvn clean verify

#### Run

The webapp can be run with the following command:

    mvn spring-boot:run

Or after building the project with `mvn clean verify` you can run the executable war.

    java -jar target/scratch-spring-rest-1.0-SNAPSHOT.war

This will start the server which can be accessed at [http://localhost:8080/rest/](http://localhost:8080/rest/ "scratch-spring-rest")

It is also possible to carry out CRUD operations on simple users:

###### Create
    $ curl -XPOST -H "Accept:application/json" -H "Content-Type:application/json" http://localhost:8080/rest/users -d '{
        "email": "some.one@there.com",
        "firstName": "Some",
        "lastName": "One",
        "phoneNumber": "5551234",
        "address": {
            "number": 3,
            "street": "That Road",
            "suburb": "This Place",
            "city": "Your City",
            "postcode": "ABC123"
        }
    }'

###### Retrieve
    $ curl -XGET -H "Accept:application/json" http://localhost:8080/rest/users
    $ curl -XGET -H "Accept:application/json" http://localhost:8080/rest/users/1

###### Update
    $ curl -XPUT -H "Content-Type:application/json" http://localhost:8080/rest/users/1 -d '{
        "email": "some.one@there.com",
        "firstName": "Some",
        "lastName": "Two",
        "phoneNumber": "5551234",
            "address": {
                "id": 1,
                "number": 3,
                "street": "That Road",
                "suburb": "This Place",
                "city": "Your City",
                "postcode": "ABC123"
            }
    }'

###### Delete
    $ curl -XDELETE -H "Accept:application/json" http://localhost:8080/rest/users/1


The  webapp only contains four classes:

The controller class that handles the `/rest/` request mapping.

[`scratch.spring.webapp.controller.ScratchController`](https://github.com/karlbennett/scratch-spring-webapp/blob/master/src/main/java/rest/webapp/controller/ScratchController.java "ScratchController")

The controller class that handles the `/rest/users` and `/rest/users/{id}` request mappings.

[`scratch.spring.webapp.controller.UserController`](https://github.com/karlbennett/scratch-spring-webapp/blob/master/src/main/java/rest/webapp/controller/UserController.java "UserController")

The the application class that starts and configures Spring Boot.

[`scratch.spring.webapp.Application`](https://github.com/karlbennett/scratch-spring-webapp/blob/master/src/main/java/rest/webapp/Application.java "Application")

The repository class that is used to persisted the `User` class.

[`scratch.spring.webapp.data.UserRepository`](https://github.com/karlbennett/scratch-spring-webapp/blob/master/src/main/java/rest/webapp/data/UserRepository.java "UserRepository")

There are also two configuration files:

The maven [`pom.xml`](https://github.com/karlbennett/scratch-spring-webapp/blob/master/pom.xml "pom.xml") file, this contains the plugin configurations and the dependencies for the project.

The [`application.properties`](https://github.com/karlbennett/scratch-spring-webapp/blob/master/application.properties "application.properties") file, this currently only contains the log levels for the application, but could contain any properties that are relevant to the application.

That is the entire project, have fun :)
