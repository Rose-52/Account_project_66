package com.cuit.tools;

import com.cuit.domain.Account;

import java.util.List;

public class PrintAccountUtil {
    public static void printAccount(List<Account> accounts) {
        System.out.println("编号\t\t账户名称\t\t金额\t\t账户\t\t创建时间\t\t描述\t\t用户名");
        for (Account account : accounts){
            System.out.println(account.toString());
        }
    }
}
