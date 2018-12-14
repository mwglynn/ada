# ada

This is the ada chat-app! We implemented the following user stories: 

1. As someone who changes location often, I would like to be able to send messages to people even when we are not in the same room, so that I can always have the option of communication.

```
remote message passing, client --> server --> client
```

2. As someone who logs on and off frequently, I would like to have a persistent account, so I can log on and off.

```
Database storage, after starting adaClient, you are prompted to enter your username
```

3. As someone who is vision-impaired, I want to be able to have text messages read aloud to me, so that I don't miss anything.

```
Text to Speech, messages are spoken as they are received
```

4. As an avid chatter (i.e., someone whose messaging can span days or weeks), I would like to see my older messages, so I can pick up a thread of conversation where I left it.

```
"History" feauture, showing previous chat messages
```

Class assignments may be found [here](https://github.com/LooseScruz/ada/tree/master/4156_submissions)

Please see the following sections for step by step instructions on how to build, test, install and operate this application.

## Usage Instructions - operating
Follow the installation instructions for Postgres and text to speech. Both of these services should be install and running prior to using ada. Currently, ada assumes that your username and password are postgres, and that you have a Google Cloud account with sufficient credits.  

- If AdaServer is not running in the cloud, build and run ada.AdaServer:
```mvn exec java@Server```

- Build and run more than one ada.AdaClient
    - use keyboard input to communicate among the clients:
```mvn exec java@Client```

When a client is up, it will prompt you to enter username information (User Story 2). At that point you can start sending messages to other clients (User Story 1), which uses text to speech (User Story 3) to audibly speak received messages. If you type `:history:`, you will see your chat history appear (User Story 4).

## Text to Speech

Set GOOGLE_APPLICATION_CREDENTIALS by following the instructions here:
https://cloud.google.com/docs/authentication/getting-started

## Database 

Postgres will be used. It should be installed for the database to function properly. Please follow Postgres instructions for your respective operating system. https://www.postgresql.org

Here's some background information on our schema:
```$sql
---------------------------------------------------/* Creation */

CREATE TABLE IF NOT EXISTS adaUser (
	ID SERIAL NOT NULL,			-- unique identification 
	userName VARCHAR(255) UNIQUE NOT NULL, 	-- user picks
	PRIMARY KEY (ID, userName)		-- we may have multiple users with the same userName
);

CREATE TABLE IF NOT EXISTS adaChat (
	ID SERIAL NOT NULL,			-- a unique identifier
	time TIME NOT NULL,			-- pulled from postgres function
	date DATE NOT NULL,			-- pulled from postgres function
	message TEXT,		
	sender VARCHAR(255),			-- one sender
	receiver VARCHAR(255),			-- stacks up, will pull down with SQL
	PRIMARY KEY (ID),
	FOREIGN KEY (sender) REFERENCES adaUser(userName) ON DELETE NO ACTION
);
```


# Misc.

## Software Engineering Principles 
[![Build Status](https://travis-ci.org/LooseScruz/ada.svg?branch=master)](https://travis-ci.org/LooseScruz/ada)


```$xslt
Code Coverage			Jacoco
Version Control:                Github
Build Tool:                     Maven 
Continuous Integration:         TravisCI
Project Management:             Trello
Coding Style:                   Google Java
Static Analysis:                PMD
Unit Testing:                   Junit
```

## pre-commit and post-commit

```$xslt
Pre-commit:                     see /PRECOMMIT
                                git hooks on google java style checker 
                                - see /.git/hooks/pre-commit executable
                                - see https://github.com/cristianoliveira/java-checkstyle
Post-commit:			see /POSTCOMMIT
				.travis.yml 
                                - mvn clean validate compile verify
```

## Reporting

```$xslt
Code Coverage			/codeCoverage/jacoco (latest version is zipped)
PMD static analysis output      /target/site/pmd.html 
Build tool                      /POM.xml
Continuous Integration          /.travis.yml          
google style checker output     /target/checkstyle-checker.xml 
unit testing output             /target/surefire-reports/ 
```
