package com.vexus2.jenkins.chatwork.jenkinschatworkplugin
import hudson.EnvVars
import hudson.model.AbstractBuild
import hudson.model.TaskListener
import hudson.util.LogTaskListener
import spock.lang.Specification
import spock.lang.Unroll

import java.util.logging.Level
import java.util.logging.Logger

import static org.mockito.Mockito.*

class BuildVariableUtilSpec extends Specification{
  static final Logger LOGGER = Logger.getLogger(BuildVariableUtilSpec.class.getName());

  @Unroll
  def "resolve"(){
    setup:
    TaskListener listener = new LogTaskListener(LOGGER, Level.INFO)
    AbstractBuild  mockBuild = mock(AbstractBuild)

    when(mockBuild.getEnvironment(any(TaskListener))).thenReturn(new EnvVars(['JAVA_HOME': '/Library/Java/JavaVirtualMachines/1.7.0u.jdk/Contents/Home']))
    when(mockBuild.getBuildVariables()).thenReturn(['BUILD_NUMBER': '123'])

    Map<String, String> extraVariables = ["BUILD_RESULT": "SUCCESS"]

    expect:
    BuildVariableUtil.resolve(source, mockBuild, listener, extraVariables) == expected

    where:
    source                          | expected
    'BUILD_NUMBER is $BUILD_NUMBER' | "BUILD_NUMBER is 123"
    'JAVA_HOME is $JAVA_HOME'       | "JAVA_HOME is /Library/Java/JavaVirtualMachines/1.7.0u.jdk/Contents/Home"
    'BUILD_RESULT is $BUILD_RESULT' | "BUILD_RESULT is SUCCESS"
  }
}
