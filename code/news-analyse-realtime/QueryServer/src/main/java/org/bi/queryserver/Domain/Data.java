package org.bi.queryserver.Domain;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Data {
    public String start_time;
    public String end_time;
    public String getStart_time() {
        String inputDate = start_time;
        // 定义输入格式
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        // 解析输入日期字符串
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter);
        // 转换为LocalDateTime
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
        // 定义输出格式
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String outputDate = localDateTime.format(outputFormatter);
        return outputDate;
    }

    public Data() {
    }

    public Data(String start_time, String end_time, List<Group> group) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.group = group;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        String inputDate = end_time;
        // 定义输入格式
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        // 解析输入日期字符串
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter);
        // 转换为LocalDateTime
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
        // 定义输出格式
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String outputDate = localDateTime.format(outputFormatter);
        return outputDate;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }


    public List<Group> group;

    public List<Group> getGroup() {
        return group;
    }

    public void setGroup(List<Group> group) {
        this.group = group;
    }
}
