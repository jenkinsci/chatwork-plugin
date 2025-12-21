package com.vexus2.jenkins.chatwork.jenkinschatworkplugin
import hudson.EnvVars
import hudson.model.AbstractBuild
import hudson.model.Result
import hudson.model.TaskListener
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import spock.lang.Specification
import spock.lang.Unroll

import static org.mockito.Matchers.*
import static org.mockito.Mockito.*

@RunWith(Enclosed)
class ChatworkPublisherSpec {
  static String readFixture(String fileName){
    ChatworkPublisherSpec.class.getClassLoader().getResource("com/vexus2/jenkins/chatwork/jenkinschatworkplugin/${fileName}").getText()
  }

  static class analyzePayload extends Specification {
    def "should create PullRequest message"() {
      when:
      // via. https://developer.github.com/v3/pulls/#get-a-single-pull-request
      String parameterDefinition = readFixture("payload_PullRequest.json")

      String expected = """
octocat created Pull Request into Hello-World,

new-feature
https://github.com/octocat/Hello-World/pull/1
""".trim()

      then:
      ChatworkPublisher.analyzePayload(parameterDefinition) == expected
    }

    def "should create compare message"() {
      when:
      String parameterDefinition = readFixture("payload_compare.json")

      String expected = """
octocat pushed into Hello-World,
- 1st commit
- 2nd commit

https://github.com/octocat/Hello-World/compare/master...topic
""".trim()

      then:
      ChatworkPublisher.analyzePayload(parameterDefinition) == expected
    }

    def "When neither PullRequest nor compare, should return empty string"(){
      when:
      String parameterDefinition = readFixture("payload_empty.json")

      then:
      ChatworkPublisher.analyzePayload(parameterDefinition) == ""
    }
  }

  static class resolveMessage extends Specification {
    ChatworkPublisher publisher

    def setup(){
      // via. https://gist.github.com/gjtorikian/5171861
      String payload = readFixture("payload_webhook.json")
      publisher = new ChatworkPublisherBuilder().rid("00000000").
          successMessage('$PAYLOAD_SUMMARY').notifyOnSuccess(true).notifyOnFail(true).build()

      AbstractBuild mockBuild = mock(AbstractBuild)
      when(mockBuild.getBuildVariables()).thenReturn(['payload': payload])
      when(mockBuild.getEnvironment(any(TaskListener))).thenReturn(new EnvVars(['JAVA_HOME': '/Library/Java/JavaVirtualMachines/1.7.0u.jdk/Contents/Home']))
      when(mockBuild.getResult()).thenReturn(Result.SUCCESS)

      publisher.build = mockBuild
    }

    def "When contains payload param, should resolve payload"(){
      when:
      String excepted = """
Garen Torikian pushed into testing,
- Test
- This is me testing the windows client.
- Rename madame-bovary.txt to words/madame-bovary.tx...

https://github.com/octokitty/testing/compare/17c497ccc7cc...1481a2de7b2a
""".trim()

      then:
      publisher.resolveMessage() == excepted
    }
  }

  static class getJobResultMessage extends Specification {
    @Unroll
    def "should return message"(){
      setup:
      ChatworkPublisher publisher = new ChatworkPublisherBuilder().
          successMessage("successMessage").
          failureMessage("failureMessage").
          unstableMessage("unstableMessage").
          notBuiltMessage("notBuiltMessage").
          abortedMessage("abortedMessage").
          build()

      expect:
      publisher.getJobResultMessage(result) == expected

      where:
      result           | expected
      Result.SUCCESS   | "successMessage"
      Result.FAILURE   | "failureMessage"
      Result.UNSTABLE  | "unstableMessage"
      Result.NOT_BUILT | "notBuiltMessage"
      Result.ABORTED   | "abortedMessage"
    }
  }
}
