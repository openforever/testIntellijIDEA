package org.smart4j.chapter2.model;

/**
 * Created by snow on 2016/4/16.
 * mysql用反引号而不是单引号来区分保留字和普通字符 `
 * create table `customer`()  反引号
 */
public class Customer {
    private long id;
    private String name;
    private String contact; //联系人
    private String telephone;
    private String email;
    private String remark;//备注

    public Customer(){}

    public Customer(long id, String name, String contact, String telephone, String email, String remark) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.telephone = telephone;
        this.email = email;
        this.remark = remark;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
