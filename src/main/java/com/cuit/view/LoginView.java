package com.cuit.view;

import com.cuit.domain.User;
import com.cuit.service.UserService;

import java.sql.SQLException;
import java.util.Scanner;

public class LoginView {
    private UserService userService = new UserService();
    User user;
    //登陆注册视图
    public User userView() throws SQLException {
	System.out.println("【MASTER】用户正在登录系统...");
        System.out.println("【INFO】开始登录流程...");
        Scanner login_choose = new Scanner(System.in);
        while(true) {
            System.out.println("请输入你的选择，1登录 2注册：");
            switch (login_choose.nextInt()) {
                case 1:
                    if((user=login())!=null){
                        return user;
                    }
                    break;
                case 2:
                    register();
                    break;
            }
        }
    }

    //注册
    public void register() throws SQLException {
        Scanner message = new Scanner(System.in);
        User user = new User();
        System.out.println("输入用户名：");
        user.setUsername(message.nextLine());
        System.out.println("输入密码：");
        user.setPassword(message.nextLine());
        if(userService.register(user)){
            System.out.println("注册成功！");
        }else
            System.out.println("用户已存在");
    }

    //登录
    public User login()  {
        Scanner message = new Scanner(System.in);
        System.out.println("输入用户名：");
        String username = message.nextLine();
        System.out.println("输入密码：");
        String password = message.nextLine();
        try {
            if(userService.login(username,password)){
                System.out.println("登录成功！");
                return new User(username,password);
            }else
                System.out.println("用户名或密码错误");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
