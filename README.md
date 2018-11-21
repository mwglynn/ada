# ada

This is a first-phase MVP of the chat-app! The app sets up a simple client and server. A client can send a message to the server to have it relayed to the other clients. 


### 1. Software Engineering Principles 
[![Build Status](https://travis-ci.org/LooseScruz/ada.svg?branch=develop)](https://travis-ci.org/LooseScruz/ada)


```$xslt
Version Control:                Github
Build Tool:                     Maven 
Continuous Integration:         TravisCI
Project Management:             Trello
Coding Style:                   Google Java
Static Analysis:                PMD
Unit Testing:                   Junit
```

### 2. pre-commit and post-commit

```$xslt
Pre-commit:                     see /PRECOMMIT
                                git hooks on google java style checker 
                                - see /.git/hooks/pre-commit executable
                                - see https://github.com/cristianoliveira/java-checkstyle
Post-commit:			see /POSTCOMMIT
				.travis.yml 
                                - mvn clean validate compile verify
```


### 3. Reporting

```$xslt
PMD static analysis output      /target/site/pmd.html 
Build tool                      /POM.xml
Continuous Integration          /.travis.yml          
google style checker output     /target/checkstyle-checker.xml 
unit testing output             /target/surefire-reports/  
```


### Build Instructions
- Build and run ada.AdaServer
- Build and run **more than one** ada.AdaClient
    - use keyboard input to communicate among the clients
