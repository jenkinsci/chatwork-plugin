package com.vexus2.jenkins.chatwork.jenkinschatworkplugin;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.EnvVars;
import hudson.util.VariableResolver;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatworkPublisherTest {
  private static String readFixture(String name) throws Exception {
    try (var stream = ChatworkPublisherTest.class.getResourceAsStream(name)) {
      return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  static Stream<Arguments> payloads() {
    return Stream.of(
        Arguments.of("payload_PullRequest.json", """
            octocat created Pull Request into Hello-World,

            new-feature
            https://github.com/octocat/Hello-World/pull/1"""),
        Arguments.of("payload_compare.json", """
            octocat pushed into Hello-World,
            - 1st commit
            - 2nd commit

            https://github.com/octocat/Hello-World/compare/master...topic"""),
        Arguments.of("payload_empty.json", ""));
  }

  @ParameterizedTest
  @MethodSource("payloads")
  void analyzesPayload(String fixture, String expected) throws Exception {
    Method method = ChatworkPublisher.class.getDeclaredMethod("analyzePayload", String.class);
    method.setAccessible(true);
    assertEquals(expected.replace("\n", System.lineSeparator()), method.invoke(null, readFixture(fixture)));
  }

  @Test
  void resolvesPayloadSummary() throws Exception {
    var publisher = new ChatworkPublisherBuilder().rid("00000000")
        .successMessage("$PAYLOAD_SUMMARY").notifyOnSuccess(true).notifyOnFail(true).build();
    AbstractBuild<?, ?> build = mock(AbstractBuild.class);
    Map<String, String> variables = Map.of("payload", readFixture("payload_webhook.json"));
    when(build.getBuildVariables()).thenReturn(variables);
    when(build.getBuildVariableResolver()).thenReturn(new VariableResolver.ByMap<>(variables));
    when(build.getEnvironment(any(TaskListener.class))).thenReturn(new EnvVars());
    when(build.getResult()).thenReturn(Result.SUCCESS);
    Field buildField = ChatworkPublisher.class.getDeclaredField("build");
    buildField.setAccessible(true);
    buildField.set(publisher, build);
    Field listenerField = ChatworkPublisher.class.getDeclaredField("listener");
    listenerField.setAccessible(true);
    listenerField.set(publisher, mock(BuildListener.class));
    Method method = ChatworkPublisher.class.getDeclaredMethod("resolveMessage");
    method.setAccessible(true);
    String expected = """
        Garen Torikian pushed into testing,
        - Test
        - This is me testing the windows client.
        - Rename madame-bovary.txt to words/madame-bovary.tx...

        https://github.com/octokitty/testing/compare/17c497ccc7cc...1481a2de7b2a""";
    assertEquals(expected.replace("\n", System.lineSeparator()), method.invoke(publisher));
  }

  static Stream<Arguments> results() {
    return Stream.of(
        Arguments.of(Result.SUCCESS, "successMessage"),
        Arguments.of(Result.FAILURE, "failureMessage"),
        Arguments.of(Result.UNSTABLE, "unstableMessage"),
        Arguments.of(Result.NOT_BUILT, "notBuiltMessage"),
        Arguments.of(Result.ABORTED, "abortedMessage"));
  }

  @ParameterizedTest
  @MethodSource("results")
  void selectsResultMessage(Result result, String expected) throws Exception {
    var publisher = new ChatworkPublisherBuilder()
        .successMessage("successMessage").failureMessage("failureMessage")
        .unstableMessage("unstableMessage").notBuiltMessage("notBuiltMessage")
        .abortedMessage("abortedMessage").build();
    Method method = ChatworkPublisher.class.getDeclaredMethod("getJobResultMessage", Result.class);
    method.setAccessible(true);
    assertEquals(expected, method.invoke(publisher, result));
  }
}
