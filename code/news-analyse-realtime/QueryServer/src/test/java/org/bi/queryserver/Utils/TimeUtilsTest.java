package org.bi.queryserver.Utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

@SpringBootTest
public class TimeUtilsTest {

    @Test
    public void testInstant(){
        Instant instant1 = TimeUtils.stringToInstant("2019-06-13 00:00:00");
        Instant instant2 = TimeUtils.stringToInstant("2019-06-13 23:59:59");
        System.out.println("ins" + instant1);
        System.out.println(TimeUtils.splitInstants(instant1,instant2,20));

    }
}

