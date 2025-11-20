package com.mazedata;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A test {@link Handler} which can be used in various tests.
 */
public class TestHandler extends Handler {
    private final List<LogRecord> records = new ArrayList<>();

    @Override
    public void publish(LogRecord record) {
        records.add(record);
    }

    @Override public void flush() {}
    @Override public void close() {}

    public List<LogRecord> getRecords() { return records; }

    public void reset() { records.clear(); }

    public void assertMessageIsLogged(String message) {
        assertMessageIsLogged(message, 1);
    }

    public void assertMessageIsLogged(String message, int recordCount) {
        assertTrue(
                records.size() == recordCount && records.stream()
                        .anyMatch(r -> r.getMessage().contains(message)),
                String.format(
                        "Expected logging message \"%s\" not found.%sLogged messages:%s%s",
                        message,
                        System.lineSeparator(),
                        System.lineSeparator(),
                        records.stream()
                                .map(LogRecord::getMessage)
                                .collect(Collectors.joining(System.lineSeparator()))
                )
        );
    }
}
