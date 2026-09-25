package com.vexus2.jenkins.chatwork.jenkinschatworkplugin;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.TaskListener;
import hudson.util.VariableResolver;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BuildVariableUtilTest {
  @ParameterizedTest
  @CsvSource({
      "BUILD_NUMBER is $BUILD_NUMBER, BUILD_NUMBER is 123",
      "JAVA_HOME is $JAVA_HOME, JAVA_HOME is /path/to/java",
      "BUILD_RESULT is $BUILD_RESULT, BUILD_RESULT is SUCCESS"
  })
  void resolvesVariables(String source, String expected) throws Exception {
    AbstractBuild<?, ?> build = mock(AbstractBuild.class);
    when(build.getEnvironment(any(TaskListener.class))).thenReturn(new EnvVars("JAVA_HOME", "/path/to/java"));
    when(build.getBuildVariableResolver()).thenReturn(new VariableResolver.ByMap<>(Map.of("BUILD_NUMBER", "123")));

    assertEquals(expected, BuildVariableUtil.resolve(source, build, TaskListener.NULL, Map.of("BUILD_RESULT", "SUCCESS")));
  }
}
