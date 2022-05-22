package xyz.fumarase.killer.anlaiye.job;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TerminalReporter implements Reporter {
    @Override
    public void report(String msg) {
        log.info(msg);
    }
}
