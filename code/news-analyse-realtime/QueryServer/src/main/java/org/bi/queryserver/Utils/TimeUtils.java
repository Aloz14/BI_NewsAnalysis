package org.bi.queryserver.Utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeUtils {
    /**
     * 实现 yyyy-MM-dd HH:mm:ss 格式的时间字符串转为Instant对象
     * @param str
     * @return Instant
     */
    public static final Instant stringToInstant(String str) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(str, dateTimeFormatter);
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return instant;
    }

    /**
     * 重载函数，接收字符串
     *
     * @param start
     * @param end
     * @return List
     */
    public static final List<Instant> splitInstants(String start, String end,int segNum){
        return splitInstants(stringToInstant(start), stringToInstant(end),segNum);
    }

    /**
     * 选择合适的时间切片对start和end进行切分
     *
     * @param start
     * @param end
     * @return List
     */
    public static final List<Instant> splitInstants(Instant start, Instant end, int segNum) {
        List<Instant> instants = new ArrayList<>();
        Duration duration = Duration.between(start, end);

        long seconds = duration.getSeconds();

        seconds = seconds / segNum;
        Instant base = start;
        instants.add(base);
        for (int i = 1; i <= segNum; i++) {
            base = base.plusSeconds(seconds);
            instants.add(base);
        }

        return instants;
    }
}
