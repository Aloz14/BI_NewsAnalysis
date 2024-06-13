package org.bi.queryserver.Utils;

import org.bi.queryserver.DAO.MySQLDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class PerformanceLogger {
    private long startTime;
    private long endTime;
    private String sqlContent;
    private Timestamp triggerTime;

    public void start() {
        startTime = System.nanoTime();
        triggerTime = Timestamp.valueOf(LocalDateTime.now());
    }

    public void stop() {
        endTime = System.nanoTime();
    }

    public String getSqlContent() {
        return sqlContent;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public Timestamp getTriggerTime() {
        return triggerTime;
    }

    public long getElapsedTimeInMilliseconds() {
        return (endTime - startTime) / 1000000; // 转换为毫秒
    }

    public void logPerformance() {
        long elapsedTime = getElapsedTimeInMilliseconds();
        System.out.println("Task completed in " + elapsedTime + " ms.");
    }

    public void writeToMySQL(MySQLDAO mysqlDAO) {
        final String TABLE_NAME = "sql_log";
        final String OPERATION = "insert into";


        mysqlDAO.insert(
                OPERATION + " " + TABLE_NAME + " (sql_content, time_spent, sql_trigger_timestamp) " +
                        "VALUES (?, ?, ?);",
                sqlContent, getElapsedTimeInMilliseconds(), getTriggerTime()
        );
    }

}
