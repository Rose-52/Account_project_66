package com.cuit.tools;


import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileReader;
import java.util.Properties;


/*
 *数据库连接工具类
 */
public class DBUtil {
    private static DataSource dataSource=new BasicDataSource();
    static {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("D:/JavaClass/zuoye014_04/丁伍峰/Account_project/jdbc.properties"));
            dataSource= BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static DataSource getDataSource(){
        return dataSource;
    }
}