package org.bi.queryserver.Utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TimeUtils {
    public static final Instant StringToInstant(String str) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(str, dateTimeFormatter);
        ZoneOffset offset = OffsetDateTime.now().getOffset();
        Instant instant = localDateTime.toInstant(offset);
        return instant;
    }
}
