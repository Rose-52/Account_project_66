package com.cuit.service;

import com.cuit.domain.User;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class ClientService {
    private static final Integer PORT=10028;
    private String basePath = "D:\\JavaClass\\zuoye014_04\\丁伍峰\\Account_project\\";
    private String downloadPath = "D:\\JavaClass\\zuoye014_04\\丁伍峰\\Account_project\\download\\";
    public HashSet<String> uploadFileList = new HashSet<>();  //存储已经上传的账务文件名
    // 上传账务文件

    public void uploadAccount(User user, String ip, String[] dateStrs){
        //筛选不存在的文件
        List<String> dateList = new ArrayList<>();
        for(String dateStr : dateStrs){
            String filePath = basePath + user.getUsername() + "\\" + dateStr + ".txt";
            File file = new File(filePath);
            if(!file.exists()){
                System.out.println("文件"+file.getName()+"不存在");
                continue;
            }
            uploadFileList.add(dateStr);
            dateList.add(dateStr);
        }
        if(dateList.isEmpty())
            return;

        try (Socket socket = new Socket(ip,PORT);
             BufferedWriter bw= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            //查看文件夹
            File filedir = new File(basePath + user.getUsername());
            if (!filedir.exists()) {
                filedir.mkdirs();
            }


            // 发送请求头，只发送一次
            bw.write("ACCOUNT_UPLOAD");
            bw.newLine();

            //上传多个文件
            for (String finalDateStr : dateList){
                String filePath = basePath + user.getUsername() + "\\" + finalDateStr + ".txt";
                File file = new File(filePath);
                if (!file.exists()) {
                    System.out.println("文件"+finalDateStr + ".txt不存在: ");
                    continue;
                }

                //用户名和文件名
                bw.write(user.getUsername()+"/"+ file.getName());
                bw.newLine();

                // 发送文件内容
                BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
                String line;
                while ((line = fileReader.readLine()) != null) {
                    bw.write( line);
                    bw.newLine();
                }

                // 发送结束标记
                bw.write("END");
                bw.newLine();
                bw.flush();

                // 等待服务器响应
                String response = br.readLine();
                if ("SUCCESS".equals(response)) {
                    System.out.println("文件"+finalDateStr+".txt上传成功: ");
                } else {
                    System.err.println("文件"+finalDateStr+".txt上传失败: ");
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 下载账务文件
    public void downloadAccount(User user, String ip) {
        try(Socket socket = new Socket(ip, PORT);
                PrintWriter pw=new PrintWriter(socket.getOutputStream(),true);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            //传输请求和用户名
            pw.println("ACCOUNT_DOWNLOAD");
            pw.println(user.getUsername());

            //接收文件列表
            String fileList = br.readLine();
            System.out.println(fileList);

            System.out.println("输入你要下载的文件编号,以\",\"分隔");
            //请求下载文件传给服务端
            pw.println(new Scanner(System.in).nextLine());

            //接收文件
            String line=null;
            while((line=br.readLine())!=null){
                //接收文件名
                String filename=line;
                //创建目录和文件夹
                File dir = new File(downloadPath);
                if(!dir.exists()){
                    dir.mkdir();
                }
                File file = new File(dir,filename);

                try(BufferedWriter fileWriter=new BufferedWriter(new FileWriter(file))){
                    while(!"END".equals(line=br.readLine())){
                        fileWriter.write(line);
                        fileWriter.newLine();
                    }
                    System.out.println("下载完毕");
                }catch (IOException e){
                    System.out.println("文件"+filename+"下载失败");
                    e.printStackTrace();
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 上传任意文件
    public void uploadFile(User user,String ip, String filePath) {
        //检查文件
        File file = new File(filePath);
        if(!file.exists()){
            System.out.println("文件不存在");
            return;
        }

        try(Socket socket = new Socket(ip, PORT);

            BufferedInputStream fileInput=new BufferedInputStream(new FileInputStream(filePath));){
            PrintWriter pr = new PrintWriter(socket.getOutputStream(),true);

            //发送请求和用户名，文件名
            pr.println("FILE_UPLOAD");
            pr.println(user.getUsername()+"|"+file.getName());

            //读取文件
            byte[] buffer = new byte[8192];
            int len;
            BufferedOutputStream bos=new BufferedOutputStream(socket.getOutputStream());
            while((len=fileInput.read(buffer))!=-1){
                bos.write(buffer,0,len);
            }
            bos.flush();
            //通知服务端输出完毕

            System.out.println(file.getName()+"上传成功");
        } catch (IOException e) {
            System.out.println("文件上传失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void downloadFile(User user,String ip)  {
        //创建目录
        File dir = new File(downloadPath);
        if(!dir.exists()){
            dir.mkdir();
        }

        try(Socket socket = new Socket(ip, PORT);
            BufferedInputStream bis=new BufferedInputStream(socket.getInputStream());){
            PrintWriter pw = new PrintWriter(socket.getOutputStream(),true);
            //传输请求和用户名
            pw.println("FILE_DOWNLOAD");
            pw.println(user.getUsername());

            //接收文件列表
            String fileList=new BufferedReader(new InputStreamReader(bis)).readLine();
            System.out.println(fileList);

            //输入想要下载文件序号
            System.out.println("输入你要下载的文件编号");
            int targetNum = new Scanner(System.in).nextInt();
            pw.println(targetNum+"");

            //接收文件名
            String fileName=new BufferedReader(new InputStreamReader(bis)).readLine();
            if("不存在".equals(fileName)){
                System.out.println("该序号不存在");
                return;
            }
            //下载文件
            byte[] buffer=new byte[1024];
            int len;
            BufferedOutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(new File(downloadPath + fileName)));
            while ((len=bis.read(buffer))!=-1) {
                fileOutput.write(buffer,0,len);
            }
            fileOutput.close();
            System.out.println(fileName+"下载成功");
        }catch (IOException e){
            System.out.println("下载失败");
            e.printStackTrace();
        }
    }


}
