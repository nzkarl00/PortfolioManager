# SENG302 - Team 700 "Space Oddities" Application

Basic project template using `gradle`, `Spring Boot`, `Thymeleaf` and `Gitlab CI`.

> This should be your project's README (renamed to `README.md`) that your team will continually update as your team progresses throughout the year.
> 
> Update this document as necessary.

## Basic Project Structure

- `systemd/` - This folder includes the systemd service files that will be present on the VM, these can be safely ignored.
- `runner/` - These are the bash scripts used by the VM to execute the application.
- `shared/` - Contains (initially) some `.proto` contracts that are used to generate Java classes and stubs that the following modules will import and build on.
- `identityprovider/` - The Identity Provider (IdP) is built with Spring Boot, and uses gRPC to communicate with other modules. The IdP is where we will store user information (such as usernames, passwords, names, ids, etc.).
- `portfolio/` - The Portfolio module is another fully fledged Java application running Spring Boot. It also uses gRPC to communicate with other modules.


## How to run

### 1 - Generating Java dependencies from the `shared` class library
The `shared` class library is a dependency of the two main applications, so before you will be able to build either `portfolio` or `identityprovider`, you must make sure the shared library files are available via the local maven repository.

Assuming we start in the project root, the steps are as follows...

On Linux: 
```
cd shared
./gradlew clean
./gradlew publishToMavenLocal
```

On Windows:
```
cd shared
gradlew clean
gradlew publishToMavenLocal
```

*Note: The `gradle clean` step is usually only necessary if there have been changes since the last publishToMavenLocal.*

### 2 - Identity Provider (IdP) Module
Assuming we are starting in the root directory...

On Linux:
```
cd identityprovider
./gradlew bootRun
```

On Windows:
```
cd identityprovider
gradlew bootRun
```

By default, the IdP will run on local port 9002 (`http://localhost:9002`).

### 3 - Portfolio Module
Now that the IdP is up and running, we will be able to use the Portfolio module (note: it is entirely possible to start it up without the IdP running, you just won't be able to get very far).

From the root directory (and likely in a second terminal tab / window)...
On Linux:
```
cd portfolio
./gradlew bootRun
```

On Windows:
```
cd portfolio
gradlew bootRun
```

By default, the Portfolio will run on local port 9000 (`http://localhost:9000`)

### 4 - Interacting

Navigate with your browser to 'http://localhost:9000/login'.
From here you may either register a user or login to the default user account.

## Default Account Credentials
#### UserName:

#### Password:

## Dependencies

### Bootstrap
Bootstrap is an open-source CSS framework used in this application for development of the front-end of the application.
It's CSS and JS libraries allow for a responsive and flexible user interface that can be created significantly faster than
doing so without the use of existing bootstrap classes/scripts. We use version Bootstrap 5.1

### Fullcalendar
Fullcalendar is a JS library that allows for the creation of a reactive calendar on the webpage that
it utilized here to display project details and visually show the dates for sprints, deadlines, events, and
milestones to the user. The application uses version 5.11.2 of Fullcalendar.

### Axios

### Websockets

## Test Dependencies

### Selenium
Selenium webdriver is a tool that allows for the automation of frontend-web-app-testing. Using selenium allows us
to automate manual testing that sees if the user of the application can see the correct data, at the correct time, when 
the correct actions are performed. This is used as a part of the integrationTest part of our pipeline. The application uses version 3.4.0.

### JUnit


## Contributors

- SENG302 teaching team
- Lane Edwards-Brown
- Yiyang Yu
- Karl Moore
- Toby Morgan
- Lachlan Alsop
- Zoher Hussein
- Hugo Reeves

## Product License

© Space Oddities [Team-700], University of Cantebury

Licensed under the proprietary license

## References

- [Spring Boot Docs](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring JPA docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html)
- [Learn resources](https://learn.canterbury.ac.nz/course/view.php?id=13269&section=9)
