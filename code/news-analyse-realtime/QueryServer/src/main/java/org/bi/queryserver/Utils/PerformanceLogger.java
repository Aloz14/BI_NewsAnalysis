package org.bi.queryserver.Utils;

import org.bi.queryserver.DAO.MySQLDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class PerformanceLogger {
    private long startTime;
    private long endTime;
    private String sqlContent;

    public void start() {
        startTime = System.nanoTime();
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

    public long getElapsedTimeInMilliseconds() {
        return (endTime - startTime) / 1000000; // 转换为毫秒
    }

    public void logPerformance() {
        long elapsedTime = getElapsedTimeInMilliseconds();
        System.out.println("Task completed in " + elapsedTime + " ms.");
    }

    public void writeToMySQL(MySQLDAO mysqlDAO) {
        String TABLE_NAME = "sql_log";
        String OPERATION = "insert into";

        mysqlDAO.insert(
                OPERATION + " " + TABLE_NAME + " (sql_content, time_spent) VALUES (?, ?);",
                sqlContent, getElapsedTimeInMilliseconds()
        );
    }

}
