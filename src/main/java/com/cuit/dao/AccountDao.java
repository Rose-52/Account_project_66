package com.cuit.dao;

import com.cuit.domain.Account;
import com.cuit.domain.User;
import com.cuit.tools.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class AccountDao {

    DataSource dataSource= DBUtil.getDataSource();
    //查询
    public List<Account> queryAll(User user) throws SQLException {
        QueryRunner qr = new QueryRunner(dataSource);
        String sql="select * from cuit_zhangwu where username=?";
        List<Account> accounts = qr.query(
                sql,
                new BeanListHandler<>(Account.class),
                user.getUsername()
        );
        return accounts;
    }


    //多条件查询
    public List<Account> queryByDate(User user, Date start, Date end) throws SQLException {
        QueryRunner qr =new QueryRunner(dataSource);
        String sql="select * from cuit_zhangwu where createtime between ? and ? and username=?";
        List<Account> accounts = qr.query(
                sql,
                new BeanListHandler<Account>(Account.class),
                start, end ,user.getUsername()
        );
        return accounts;
    }


    //添加
    public boolean addAccount(Account account){
        QueryRunner qr = new QueryRunner(dataSource);
        String sql="insert into cuit_zhangwu values(null,?,?,?,?,?,?)";
        Object[] parame={account.getFlname(),account.getMoney(),account.getZhanghu(),account.getCreatetime(),account.getDescription(),account.getUsername()};
        try {
            qr.update(
                    sql,
                    parame
            );
        } catch (SQLException e) {
            System.out.println("添加失败");
            throw new RuntimeException(e);
        }
        return true;
    }


    //编辑
    public boolean updateAccount(User user,Integer id,Account account){
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "update cuit_zhangwu set flname=?,money=?,zhanghu=?,createtime=?,description=? where zwid=? and username=?";
        int update=0;
        Object[] parame={account.getFlname(),account.getMoney(),account.getZhanghu(),account.getCreatetime(),account.getDescription(),id,user.getUsername()};
        try {
            update = qr.update(sql,parame);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            return update>0;
        }
    }


//删除
    public boolean deleteAccount(User user,Integer id){
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "delete from cuit_zhangwu where zwid =? and username=?" ;
        int update=0;
        try {
            update = qr.update(sql,id,user.getUsername());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            return update>0;
        }
    }


    public List<Account> searchAccount(User user,String keyword){
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "select * from cuit_zhangwu where (flname like ? or money like ? or " +
                "zhanghu like ? or createtime like ? or description like ? ) and username=?";
        List<Account> accounts = null;
        keyword="%"+keyword+"%";
        try {
            accounts = qr.query(sql,new BeanListHandler<>(Account.class),keyword,keyword,keyword,keyword,keyword,user.getUsername());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accounts;
    }



    public Account queryById(User user,int id){
        QueryRunner qr = new QueryRunner(dataSource);
        String sql = "select * from cuit_zhangwu where zwid=? and username=?";
        Account accounts;
        try {
            accounts = qr.query(sql, new BeanHandler<Account>(Account.class), id,user.getUsername());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return accounts;
    }
}
