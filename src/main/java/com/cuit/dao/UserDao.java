package com.cuit.dao;

import com.cuit.domain.User;
import com.cuit.tools.DBUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import java.sql.SQLException;
import java.util.List;

public class UserDao {
    //×¢²á
    public boolean register(User user) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DBUtil.getDataSource());
        if(findUsername(user)) {
            return false;
        }
        String sql="insert into user(usernmae,password) values(?,?)";
        queryRunner.update(sql,user.getUsername(),user.getPassword());
        return true;
    }

    //µÇÂ¼
    public boolean login(String username,String password) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DBUtil.getDataSource());
        String sql="select id from user where usernmae=? and password=?";
        List<Integer> users_id = queryRunner.query(
                sql,
                new ColumnListHandler<Integer>()
                , username, password);
        if(users_id.isEmpty())
            return false;
        return true;
    }

    //¼ì²éÊÇ·ñÒÑ¾­×¢²á
    private boolean findUsername(User user) throws SQLException {
        QueryRunner queryRunner = new QueryRunner(DBUtil.getDataSource());
        String sql="select id from user where usernmae=?";
        List<Integer> ids = queryRunner.query(
                sql,
                new ColumnListHandler<>(),
                user.getUsername());
        if(ids.size()>0)
            return true;
        return false;
    }
}





