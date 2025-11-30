package com.cuit.domain;


import java.util.Date;

/*
 * 账户 实体类
 */
public class Account {
    private Integer zwid;
    private String flname;
    private double money;
    private String zhanghu;
    private Date createtime;
    private String description;
    private String username;

    public Account(String flname, double money, String zhanghu, Date createtime, String description, String username) {
        this.flname = flname;
        this.money = money;
        this.zhanghu = zhanghu;
        this.createtime = createtime;
        this.description = description;
        this.username = username;
    }

    @Override
    public String toString() {
        return zwid+"\t\t"+flname+"\t\t"+money+"\t\t"+zhanghu+"\t\t"+createtime+"\t\t"+description+"\t\t"+username;
    }

    public Account() {
    }

    public Integer getZwid() {
        return zwid;
    }

    public void setZwid(Integer zwid) {
        this.zwid = zwid;
    }

    public String getFlname() {
        return flname;
    }

    public void setFlname(String flname) {
        this.flname = flname;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getZhanghu() {
        return zhanghu;
    }

    public void setZhanghu(String zhanghu) {
        this.zhanghu = zhanghu;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}