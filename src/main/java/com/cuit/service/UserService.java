package com.cuit.service;

import com.cuit.dao.UserDao;
import com.cuit.domain.User;

import java.sql.SQLException;

public class UserService {
    UserDao userDao = new UserDao();

    //×¢²á
    public boolean register(User user) throws SQLException {
        return userDao.register(user);
    }

    //µÇÂ¼
    public boolean login(String username, String password) throws SQLException {
        return userDao.login(username,password);
    }




}
