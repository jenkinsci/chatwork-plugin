# Jenkins Chatwork Plugin
[![Plugin Version](https://img.shields.io/jenkins/plugin/v/chatwork.svg)](https://plugins.jenkins.io/chatwork)
[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/chatwork-plugin/master)](https://ci.jenkins.io/job/Plugins/job/chatwork-plugin/job/master/)

This Plugin will notify the ChatWork any message.

### Requirements

The next release requires Jenkins 2.555.3 or newer and a Jenkins-supported Java runtime (Java 21 or 25).
Upgrade Jenkins before installing this version on an older controller.

### Development

Use Maven 3.9.6 or newer and JDK 21 or 25. Check the Java version reported by Maven:

```sh
mvn --version
```

If Maven uses a different JDK, set `JAVA_HOME` to your JDK 21 or 25 installation.
Build the plugin and run the tests and static analysis:

```sh
mvn clean verify
```

The plugin archive is generated at `target/chatwork.hpi`.
The [Jenkinsfile](Jenkinsfile) configures CI to test Linux with JDK 21 and 25, and Windows with JDK 21.

### Global Configuration

![](/docs/images/1.0.0-global-config.png)

### Job Configuration

![](/docs/images/1.0.2-job-config.png)

-   **Success message** , **Failure message** , **Unstable message** ,
    **Not built message** , **Aborted message**
    -   If message is empty, used Global message
    -   emoticon
    -   chatwork tags

-   **Default message** is supported for some variables.

Examples:

-   build variables (ex. *$JOB\_NAME* , *$BUILD\_URL* )
-   environment variables (ex. *$PATH* , *$JAVA\_HOME* )
-   *$BUILD\_RESULT* (ex. *SUCCESS*, *FAILED* )
