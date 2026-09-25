package com.vexus2.jenkins.chatwork.jenkinschatworkplugin;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EscapeUtilTest {
  @ParameterizedTest
  @CsvSource({"ABC, ABC", "ＡＢＣ, ＡＢＣ", "A\bBC, ABC", "AB\u001FC, ABC"})
  void removesControlCharacters(String source, String expected) {
    assertEquals(expected, EscapeUtil.sanitize(source));
  }
}
