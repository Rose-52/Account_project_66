package com.cuit.view;

import com.cuit.domain.Account;
import com.cuit.domain.User;
import com.cuit.service.AccountService;
import com.cuit.service.ClientService;
import com.cuit.tools.PrintAccountUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MainView {
    private Scanner sc = new Scanner(System.in);
    private User user;
    private AccountService zwService = new AccountService();

    public String IP;
    public ClientService clientService = new ClientService();
    public MainView(User user){
        this.user = user;
    }
    public void mainView() throws SQLException, ParseException {
        System.out.println("欢迎使用账务系统");
        while(true) {
            System.out.println("=========================================================================================");
            System.out.println("1.查询 2.多条件查询 3.添加 4.编辑 5.删除 6.搜索 7.导出 8.上传账务 9.下载账务 10.上传文件 11.下载文件");
            System.out.println("=========================================================================================");
            switch (sc.nextLine()) {
                case "1":
                    quertAll();
                    break;
                case "2":
                    queryByDate();
                    break;
                case "3":
                    try {
                        addAccount();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "4":
                    try {
                        updateAccount();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "5":

                    try {
                        deleteAccount();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "6":
                    searchAccount();
                    break;
                case "7":
                    try {
                        exportAccount();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case "8":
                    uploadAccount();

                    break;
                case "9":
                    downloadAccount();

                    break;
                case "10":
                    uploadFile();

                    break;
                case "11":
                    downloadFile();

                    break;
                default:
                    System.out.println("输入错误");
            }
        }
    }

    private void uploadAccount(){
        System.out.println("输入服务器ip");
        IP=sc.nextLine();
        System.out.println("输入日期(yyyy-MM-dd),\",\"分隔");
        String dateStr = sc.nextLine();
        String[] split=dateStr.split(",");
        for(int i=0;i<split.length;i++)
            split[i]=split[i].trim();
        clientService.uploadAccount(user, IP, split);
    }
    private void downloadAccount() {
        System.out.println("输入服务器ip");
        IP = sc.nextLine();
        clientService.downloadAccount(user, IP);
    }

    private void uploadFile()   {
        System.out.println("输入服务器ip");
        IP=sc.nextLine();
        System.out.println("输入要上传的文件绝对路径");
        String filePath = sc.nextLine();
        //处理字符串
        clientService.uploadFile(user,IP ,filePath.replace("\"","").replace("\\","\\\\"));

    }

    private void downloadFile()  {
        System.out.println("输入服务器ip");
        IP = sc.nextLine();
        clientService.downloadFile(user,IP);
    }

    //导出账务
    private void exportAccount() throws ParseException, SQLException, IOException {
        System.out.println("请输出导出的日期或日期范围(yyyy-MM-dd),\",\"分隔");
        //获取账务内容
        List<Date> dates = getDate();
        Date startdate = dates.get(0);
        Date enddate = dates.get(1);
        List<Account> accounts = zwService.queryByDate(user,startdate, enddate);
        //写入文件
        zwService.exportAccount(accounts,user);
    }

    //模糊查询
    private void searchAccount() throws SQLException {
        System.out.println("请输入要搜索的内容");
        String keyword = sc.nextLine();
        System.out.println("搜索结果:");
        PrintAccountUtil.printAccount(zwService.searchAccount(user,keyword));
    }

    //删除记录
    private void deleteAccount() throws SQLException, IOException {
        System.out.println("请输入要删除的编号,逗号分割");
        String ids=sc.nextLine();
        for(String id_1:ids.split(",")) {
            int id = Integer.parseInt(id_1.trim());
            if(!idExistInfo(id)) {
                continue;
            }
            if (zwService.deleteAccount(user,id))
                System.out.println("删除成功");
            else
                System.out.println("删除失败");
        }
    }

    //修改记录
    private void updateAccount() throws SQLException, ParseException, IOException {
        System.out.println("该用户所有信息");
        zwService.queryAll(user);
        //查看id是否存在
        System.out.println("请输入要修改的id,逗号分隔");
        String ids=sc.nextLine();
        String[] id_str = ids.split(",");
        for(String id_1: id_str){
            Account account = new Account();
            Integer id=Integer.parseInt(id_1.trim());
            if(!idExistInfo(id)){
                continue;
            }
            System.out.println("id="+id);
            account.setZwid(id);
            System.out.println("请输入资金流向");
            account.setFlname(sc.nextLine());
            System.out.println("输入金额");
            account.setMoney(Double.parseDouble(sc.nextLine()));
            System.out.println("输入账户");
            account.setZhanghu(sc.nextLine());
            System.out.println("日期（yyyy-MM-dd）");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            account.setCreatetime(sdf.parse(sc.nextLine()));
            System.out.println("输入描述");
            account.setDescription(sc.nextLine());
            account.setUsername(user.getUsername());
            if(zwService.updateAccount(user,id,account))
                System.out.println("修改成功");
            else
                System.out.println("修改失败");
        }
    }

    private boolean idExistInfo(Integer id) {
        if(zwService.zwDao.queryById(user,id)==null){
            System.out.println("编号"+id+"不存在");
            return false;
        }
        return true;
    }

    private void addAccount() throws SQLException, IOException {
        Account account = new Account();
        System.out.println("输入资金流向");
        account.setFlname(sc.nextLine());
        System.out.println("输入金额");
        account.setMoney(Double.parseDouble(sc.nextLine()));
        System.out.println("输入账户");
        account.setZhanghu(sc.nextLine());
        System.out.println("日期（yyyy-MM-dd）");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            account.setCreatetime(sdf.parse(sc.nextLine()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println("输入描述");
        account.setDescription(sc.nextLine());
        account.setUsername(user.getUsername());
        zwService.addAccount(user,account);
    }

    private void quertAll() throws SQLException {
        System.out.println("当前用户的账户信息");
        zwService.queryAll(user);
    }
    private void queryByDate() throws SQLException, ParseException {
        System.out.println("输入1个或2个日期来查询账务(yyyy-MM-dd)");
        List<Date> dates = getDate();
        Date startdate = dates.get(0);
        Date enddate = dates.get(1);
        PrintAccountUtil.printAccount(zwService.queryByDate(user,startdate,enddate));
    }

    private List<Date> getDate() throws ParseException {
        String s = sc.nextLine().trim(); //去掉空格
        String[] split = s.split(",");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startdate = sdf.parse(split[0]);
        Date enddate=(split.length>1)?sdf.parse(split[1]):startdate;//一个参数时默认为开始时间
        return Arrays.asList(startdate,enddate);
    }




}
