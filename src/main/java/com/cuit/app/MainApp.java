package com.cuit.app;

import com.cuit.view.MainView;
import com.cuit.view.LoginView;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class MainApp {
    public static MainView mv;
    public static void main(String[] args) throws SQLException, ParseException, IOException {
        LoginView loginView = new LoginView();
        mv=new MainView(loginView.userView());
        mv.mainView();
    }
}
