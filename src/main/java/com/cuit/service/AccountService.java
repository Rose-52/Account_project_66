package com.cuit.service;

import com.cuit.app.MainApp;
import com.cuit.dao.AccountDao;
import com.cuit.domain.Account;
import com.cuit.domain.User;
import com.cuit.tools.FileWriterUtil;
import com.cuit.tools.PrintAccountUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AccountService {
    public AccountDao zwDao=new AccountDao();
    String path="D:/JavaClass/zuoye014_04/丁伍峰/Account_project/";
    //查询
    public void queryAll(User user) throws SQLException {
        PrintAccountUtil.printAccount(zwDao.queryAll(user));
    }

    //条件查询
    public List<Account> queryByDate(User user, Date start, Date end) throws SQLException {
        return zwDao.queryByDate(user,start,end);
    }


    //添加
    public boolean addAccount(User user,Account account) throws SQLException, IOException {
        boolean add = zwDao.addAccount(account);
        Date add_date=account.getCreatetime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //添加时同步
        if(modify(user,add_date,sdf.format( add_date))){
            System.out.println("同步修改文件"+sdf.format( add_date)+".txt");
        }
        return add;
    }


    public boolean updateAccount(User user,Integer id,Account account) throws SQLException, IOException {


        //同步到导出文件
        Account before_update_account = zwDao.queryById(user,id);//获取修改前的数据
        Date before=before_update_account.getCreatetime();//获取修改前的时间

        Date after = account.getCreatetime();//获取修改后的时间

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String before_date = sdf.format(before);
        String after_date = sdf.format(after);

        //数据库修改
        boolean update = zwDao.updateAccount(user, id, account);

        //获取已经上传到文件
        HashSet<String> hasUploadFile=MainApp.mv.clientService.uploadFileList;

        if(!before_date.equals(after_date))
            //修改了时间：用之前的时间同步修改前文件
            if(modify(user,before,before_date)){
                System.out.println("同步修改文件"+before_date+".txt");
                //如果文件已上传，则请求更新
                if(hasUploadFile.contains(before_date)){
                    System.out.println("同步服务器文件"+before_date+".txt");
                    MainApp.mv.clientService.uploadAccount(user,MainApp.mv.IP, new String[]{before_date});
                }
            }
        //没改就只同步修改后的文件
        if(modify(user,after,after_date)) {
            System.out.println("同步修改文件"+after_date+".txt");
            if(hasUploadFile.contains(after_date)){
                System.out.println("同步服务器文件"+after_date+".txt");
                MainApp.mv.clientService.uploadAccount(user,MainApp.mv.IP, new String[]{after_date});
            }
        }

        return update;
    }

    //增删改后同步导出的文件
    private boolean modify(User user,Date before, String update_file_name) throws SQLException, IOException {
        File[] export_file = new File(path+user.getUsername()+"/").listFiles();
        if(export_file==null)
            return false;
        for(File file: export_file) {
            String[] split = file.getName().split("\\.");
            List<Account> update_accounts = zwDao.queryByDate(user,before, before);//获取修改数据对应时间的数据
            File before_file = new File(path + user.getUsername() + "/" + update_file_name + ".txt");
            //判断修改的日期文件是否还有数据，没有就删除
            if(update_accounts.size()==0&&before_file.exists()){
                System.out.println("同步删除文件"+before_file.getName());
                before_file.delete();
                return true;
            }
            if (update_file_name.equals(split[0])) {
                //同步导出
                exportAccount(update_accounts,user);
                return true;
            }
        }
        return false;
    }

    //删除账务
    public boolean deleteAccount(User user,Integer id) throws SQLException, IOException {
            boolean delete = false;
            Account before_account = zwDao.queryById(user,id);//获取删除前的数据
            Date before_date=before_account.getCreatetime();//获取修改前的时间

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String update_file_name=sdf.format( before_date);//时间转为字符串
            delete = zwDao.deleteAccount(user,id);
            if(modify(user,before_date, update_file_name))
                System.out.println("同步修改文件"+update_file_name+".txt");

        return delete;
    }

    public List<Account> searchAccount(User user,String keyword) throws SQLException {
        return zwDao.searchAccount(user,keyword);
    }


    public void exportAccount(List<Account> accounts,User user) throws SQLException, IOException {
        File user_file = new File(path+user.getUsername()+"/");
        if(!user_file.exists())
            user_file.mkdirs();

        // 按日期分组：Date -> List<Account>
        HashMap<Date, List<Account>> dateAccounts = new HashMap<>();
        for (Account account : accounts) {
            Date date = account.getCreatetime();

            // 如果该日期不存在，初始化一个列表
            dateAccounts.computeIfAbsent(date, k -> new ArrayList<>()).add(account);
        }

        // 转换为 Date -> List<String>
        HashMap<Date, List<String>> stringContent = new HashMap<>();
        //遍历日期，日期对应的集合类型Account->String
        for (Map.Entry<Date, List<Account>> entry : dateAccounts.entrySet()) {
            List<String> stringList = entry.getValue().stream()
                    .map(Account::toString)
                    .collect(Collectors.toList());
            stringContent.put(entry.getKey(), stringList);
        }

        // 导出文件
            FileWriterUtil.writeFile(path+user.getUsername()+"/", stringContent);
    }

}
