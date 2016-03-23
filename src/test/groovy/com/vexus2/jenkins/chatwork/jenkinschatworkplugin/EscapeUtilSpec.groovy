package com.vexus2.jenkins.chatwork.jenkinschatworkplugin

import spock.lang.Specification
import spock.lang.Unroll

class EscapeUtilSpec extends Specification {
    @Unroll
    def "resolve"(){
        expect:
        EscapeUtil.sanitize(source) == expected

        where:
        source      | expected
        "ABC"       | "ABC"
        "ＡＢＣ"     | "ＡＢＣ"
        "A\bBC"     | "ABC"
        "AB\u001FC" | "ABC"
    }
}
