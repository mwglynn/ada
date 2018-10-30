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
Pre-commit:                     git hooks on google java style checker 
                                see /.git/hooks/pre-commit executable
Post-commit:                    TravisCI 
```


### 3. Reporting

```$xslt
PMD static analysis             /target/site/pmd.html           
google style checker            /target/checkstyle-checker.xml 
unit testing                    /target/surefire-reports/  
```


### Build Instructions
- Build and run AdaServer
- Build and run *more than one* AdaClient
    - use keyboard input communicate among the clients
