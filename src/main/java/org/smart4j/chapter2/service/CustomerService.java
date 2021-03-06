package org.smart4j.chapter2.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.chapter2.helper.DBHelper;
import org.smart4j.chapter2.model.Customer;

import java.util.List;
import java.util.Map;

/**
 * Created by snow on 2016/4/16.
 * 提取客户数据服务
 */
public class CustomerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    /*获取客户列表*/
    public List<Customer> getCustomerList(/*String keyword*/){
       /* Connection conn = DBHelper.getConnection();
        try {
            String sql ="SELECT * FROM customer";
            return DBHelper.queryEntityList(Customer.class, sql);
        } finally {
            DBHelper.closeConnection();每个线程只有一个conn, 在queryEntityList中关闭
        }*/
        String sql ="SELECT * FROM customer";
        return DBHelper.queryEntityList(Customer.class, sql);
    }

    /*获取客户*/
    public Customer getCustomer(long id){
        String sql = "SELECT * FROM customer WHERE id = ?";
        return DBHelper.queryEntity(Customer.class, sql, id);
    }

    /*创建客户*/
    public boolean createCustomer(Map<String, Object> fieldMap){
        return DBHelper.insertEntity(Customer.class, fieldMap);
    }

    /*更新客户*/
    public boolean updateCustomer(long id, Map<String, Object> fieldMap){
        return DBHelper.updateEntity(Customer.class, id, fieldMap);
    }

    /*删除客户*/
    public boolean deleteCustomer(long id){
        return DBHelper.deleteEntity(Customer.class, id);
    }
}
