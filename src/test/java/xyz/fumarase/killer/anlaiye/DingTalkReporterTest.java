package xyz.fumarase.killer.anlaiye;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DingTalkReporterTest {
    @Test
    void test() {
        DingTalkReporter dingTalkReporter = new DingTalkReporter();
        dingTalkReporter.info("测试报告");
    }
}