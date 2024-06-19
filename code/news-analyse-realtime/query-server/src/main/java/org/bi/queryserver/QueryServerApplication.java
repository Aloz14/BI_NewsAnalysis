package org.bi.queryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication()
public class QueryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryServerApplication.class, args);

    }

}
