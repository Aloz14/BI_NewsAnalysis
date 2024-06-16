package org.bi.queryserver.DAO;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HBaseDAO {
    @Autowired
    private Admin hbaseAdmin;

    /**
     * 判断表是否存在
     *
     * @param tableName 表名
     * @return true/false
     */
    public boolean isExists(String tableName) {
        boolean tableExists = false;
        try {
            TableName table = TableName.valueOf(tableName);
            tableExists = hbaseAdmin.tableExists(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableExists;
    }

    /**
     * 获取数据（全表数据）
     *
     * @param tableName 表名
     * @return map
     */
    public List<Map<String, String>> getData(String tableName) {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            Table table = hbaseAdmin.getConnection().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                HashMap<String, String> map = new HashMap<>();
                //rowkey
                String row = Bytes.toString(result.getRow());
                map.put("row", row);
                for (Cell cell : result.listCells()) {
                    //列族
                    String family = Bytes.toString(cell.getFamilyArray(),
                            cell.getFamilyOffset(), cell.getFamilyLength());
                    //列
                    String qualifier = Bytes.toString(cell.getQualifierArray(),
                            cell.getQualifierOffset(), cell.getQualifierLength());
                    //值
                    String data = Bytes.toString(cell.getValueArray(),
                            cell.getValueOffset(), cell.getValueLength());
                    map.put(family + ":" + qualifier, data);
                }
                list.add(map);
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取数据（根据传入的filter）
     *
     * @param tableName 表名
     * @param filter    过滤器
     * @return map
     */
    public List<Map<String, String>> getData(String tableName, Filter filter) {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            Table table = hbaseAdmin.getConnection().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            // 添加过滤器
            scan.setFilter(filter);
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                HashMap<String, String> map = new HashMap<>();
                //rowkey
                String row = Bytes.toString(result.getRow());
                map.put("row", row);
                for (Cell cell : result.listCells()) {
                    //列族
                    String family = Bytes.toString(cell.getFamilyArray(),
                            cell.getFamilyOffset(), cell.getFamilyLength());
                    //列
                    String qualifier = Bytes.toString(cell.getQualifierArray(),
                            cell.getQualifierOffset(), cell.getQualifierLength());
                    //值
                    String data = Bytes.toString(cell.getValueArray(),
                            cell.getValueOffset(), cell.getValueLength());
                    map.put(family + ":" + qualifier, data);
                }
                list.add(map);
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取数据（根据rowkey）
     *
     * @param tableName 表名
     * @param rowKey    rowKey
     * @return map
     */
    public Map<String, String> getData(String tableName, String rowKey) {
        HashMap<String, String> map = new HashMap<>();
        try {
            Table table = hbaseAdmin.getConnection().getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            Result result = table.get(get);
            if (result != null && !result.isEmpty()) {
                for (Cell cell : result.listCells()) {
                    //列族
                    String family = Bytes.toString(cell.getFamilyArray(),
                            cell.getFamilyOffset(), cell.getFamilyLength());
                    //列
                    String qualifier = Bytes.toString(cell.getQualifierArray(),
                            cell.getQualifierOffset(), cell.getQualifierLength());
                    //值
                    String data = Bytes.toString(cell.getValueArray(),
                            cell.getValueOffset(), cell.getValueLength());
                    map.put(family + ":" + qualifier, data);
                }
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取数据（根据RowKey的范围）
     *
     * @param tableName
     * @param startRowKey
     * @param endRowKey
     * @return
     */
    public List<Map<String, String>> getData(String tableName,
                                             String startRowKey, String endRowKey) {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            Table table = hbaseAdmin.getConnection().getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();
            scan.withStartRow(startRowKey.getBytes()).withStopRow(endRowKey.getBytes());

            ResultScanner resultScanner = table.getScanner(scan);
            for (Result result : resultScanner) {
                HashMap<String, String> map = new HashMap<>();
                String row = Bytes.toString(result.getRow());
                map.put("row", row);
                for (Cell cell : result.listCells()) {
                    //列族
                    String family = Bytes.toString(cell.getFamilyArray(),
                            cell.getFamilyOffset(), cell.getFamilyLength());
                    //列
                    String qualifier = Bytes.toString(cell.getQualifierArray(),
                            cell.getQualifierOffset(), cell.getQualifierLength());
                    //值
                    String data = Bytes.toString(cell.getValueArray(),
                            cell.getValueOffset(), cell.getValueLength());
                    map.put(family + ":" + qualifier, data);
                }
                list.add(map);
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取数据（根据rowkey，列族，列）
     *
     * @param tableName       表名
     * @param rowKey          rowKey
     * @param columnFamily    列族
     * @param columnQualifier 列
     * @return map
     */
    public String getData(String tableName, String rowKey, String columnFamily,
                          String columnQualifier) {
        String data = "";
        try {
            Table table = hbaseAdmin.getConnection().getTable(TableName.valueOf(tableName));
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(columnQualifier));
            Result result = table.get(get);
            if (result != null && !result.isEmpty()) {
                Cell cell = result.listCells().get(0);
                data = Bytes.toString(cell.getValueArray(), cell.getValueOffset(),
                        cell.getValueLength());
            }
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
