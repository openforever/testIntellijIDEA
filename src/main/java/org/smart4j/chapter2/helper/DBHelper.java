package org.smart4j.chapter2.helper;


import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.utils.CollectionUtil;
import org.smart4j.chapter2.utils.PropsUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private static final BasicDataSource DATA_SOURCE;

    /*private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;*/

    static {
        Properties conf = PropsUtils.loadProps("db.properties");
        String driver = conf.getProperty("jdbc.driver");
        String url = conf.getProperty("jdbc.url");
        String username = conf.getProperty("jdbc.username");
        String password = conf.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);

        /*try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e){
            LOGGER.error("can not load jdbc driver", e);
        }*/
    }

    /*执行SQL文件*/
    public static void executeSqlFile(String filePath){
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String sql = null;
        try {
            while ((sql = br.readLine()) != null){
                executeUpdate(sql);
            }
        } catch (IOException e) {
            LOGGER.error("execute sql file failure", e);
            throw new RuntimeException(e);
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
        }/* finally { 由数据库连接池dbcp自动管理
            closeConnection();
        }*/
        return entityList;
    }

    /*查询实体*/
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object ... params){
        T entity;
        try {
            Connection conn = getConnection();
            /*这些Handler是实现了ResultSetHandler*/
            entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity failure", e);
            throw new RuntimeException(e);
        }/* finally {
            closeConnection();
        }*/
        return entity;
    }

    /*插入实体, 自动构建插入的表结构SQL*/
    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap){
        if (CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("can not insert entity: fieldMap is empty");
            return false;
        }
        String sql = "INSERT INTO" + getTableName(entityClass);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder(")");
        for (String fieldName : fieldMap.keySet()){
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");

        sql += columns + " VALUES" + values;

        Object[] params = fieldMap.values().toArray();

        return executeUpdate(sql, params) == 1;

    }

    /*更新实体,自动构建更新的SQL*/
    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap){
        if (CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("can not update entity: fieldMap is empty");
            return false;
        }
        String sql = "UPDATE " + getTableName(entityClass) + " SET ";
        StringBuilder columns = new StringBuilder();
        for (String fieldName : fieldMap.keySet()){
            columns.append(fieldName).append("=?, ");
        }
        sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id = ?";
        List<Object> paramList = new ArrayList<>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();

        return executeUpdate(sql, params) == 1;
    }

    /*删除实体*/
    public static <T> boolean deleteEntity(Class<T> entityClass, long id){
        String sql = "DELETE FROM " + getTableName(entityClass) + " WHERE id = ?";
        return executeUpdate(sql, id) == 1;
    }

    /*执行查询语句，可以连接多表查询。Map表示列名与列值的映射关系*/
    public static List<Map<String, Object>> executeQuery(String sql, Object ... params){
        List<Map<String, Object>> result;
        try {
            Connection conn = getConnection();
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            LOGGER.error("execute query failure", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /*根据实体类Class对象获取对应的类名*/
    private static String getTableName(Class<?> entityClass){
        return entityClass.getSimpleName();
    }

    /*执行更新语句(update,insert,delete),返回更新影响的记录数*/
    public static int executeUpdate(String sql, Object ... params){
        int rows;
        try {
            Connection conn = getConnection();
            rows = QUERY_RUNNER.update(conn, sql, params);
        } catch (SQLException e) {
            LOGGER.error("execute update failure", e);
            throw new RuntimeException(e);
        }
        return rows;
    }

    /*获取数据库连接, 先去ThreadLocal中查找，若不存在，则创建一个新的Connection，并将其放入ThreadLocal
    * 频繁调用getConnetion会频繁创建数据库连接，会造成大量的系统开销*/
    public static Connection getConnection(){
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null){
            try {
                /*conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);*/
                conn = DATA_SOURCE.getConnection();
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
    /*使用数据库连接池DBCP，就不需要删除连接了，由连接池自动管理,避免频繁创建，删除数据库连接，消耗太多资源
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
    }*/
}
