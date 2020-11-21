# Jenkins Chatwork Plugin
[![Plugin Version](https://img.shields.io/jenkins/plugin/v/chatwork.svg)](https://plugins.jenkins.io/chatwork)
[![Build Status](https://ci.jenkins.io/buildStatus/icon?job=Plugins/chatwork-plugin/master)](https://ci.jenkins.io/job/Plugins/job/chatwork-plugin/job/master/)

This Plugin will notify the ChatWork any message.

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
