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

### Database 

Postgre will be used. It should be installed for the database to function properly

```$sql
---------------------------------------------------/* Creation */

CREATE TABLE IF NOT EXISTS adaUser (
	ID SERIAL NOT NULL,			-- unique identification 
	userName VARCHAR(255) UNIQUE NOT NULL, 	-- user picks, pulled from other source (per GUI?)
						-- TODO: check on default if none
						-- TODO: may want to remove unique constraint? 
	PRIMARY KEY (ID, userName)		-- we may have multiple users with the same userName
);

CREATE TABLE IF NOT EXISTS adaChat (
	ID SERIAL NOT NULL,			-- a unique identifier
	time_stamp TIME NOT NULL,	 
	message TEXT,		
	sender VARCHAR(255),			-- one sender
	receiver VARCHAR(255)[],		-- mulitple receivers, this cannot be foreign key (i.e., you can send messages to non-users)
	PRIMARY KEY (ID),
	FOREIGN KEY (sender) REFERENCES adaUser(userName) ON DELETE NO ACTION		-- may want to change to CASCADE
);

---------------------------------------------------/* Simple Insertion Example */

INSERT INTO adaUser (ID, userName) VALUES 
	(1, 'nathan'),
	(2, 'kevin'),
	(3, 'sally')
;

INSERT INTO adaChat (ID, time_stamp, message, sender, receiver) VALUES 
	(1, '02:03:04', 'hi there buddy', 'nathan', '{kevin, sally}'),
	(2, '02:03:05', 'oh nice too yo', 'kevin', '{nathan}'),
	(3, '02:03:06', 'hi there buddy', 'nathan', '{sally}')
;
```