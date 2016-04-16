package org.smart4j.chapter2.helper;


import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.utils.PropsUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Created by snow on 2016/4/16.
 * 数据库操作助手类
 */
public class DBHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBHelper.class);
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();
    /*确保一个线程只有一个connection，使用ThreadLocal存储本地变量，确保线程安全*/
    private static final ThreadLocal<Connection> CONNECTION_HOLDER = new ThreadLocal<>();

    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        Properties conf = PropsUtils.loadProps("db.properties");
        DRIVER = conf.getProperty("jdbc.driver");
        URL = conf.getProperty("jdbc.url");
        USERNAME = conf.getProperty("jdbc.username");
        PASSWORD = conf.getProperty("jdbc.password");

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e){
            LOGGER.error("can not load jdbc driver", e);
        }
    }

    /*查询实体列表,可变参数列表，可以是0个参数*/
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object ... params){
        List<T> entityList = null;
        try {
            Connection conn = getConnection();
            /*先执行SQL语句返回一个ResultSet，然后通过反射创建并初始化实体对象*/
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (Exception e) {
            LOGGER.error("query entity list failure", e);
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }
        return entityList;
    }

    /*获取数据库连接, 先去ThreadLocal中查找，若不存在，则创建一个新的Connection，并将其放入ThreadLocal*/
    public static Connection getConnection(){
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null){
            try {
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                LOGGER.error("get connection failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
        return conn;
    }

    /*关闭数据库连接,从ThreadLocal中移除*/
    /*public static void closeConnection(Connection conn){
        if (conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }*/
    public static void closeConnection(){
        Connection conn = CONNECTION_HOLDER.get();
        if (conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
                throw new RuntimeException(e);
            } finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }
}
