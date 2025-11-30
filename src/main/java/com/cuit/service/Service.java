package com.cuit.service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

public class Service {
    //用hashset避免重名
    private static final HashSet<String> uploadAccounts = new HashSet<>();

    public static void main(String[] args) {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(10028);
        } catch (IOException e) {
            e.printStackTrace();
        }
        handleClient(ss);
    }


    private static void handleClient(ServerSocket ss){

        while(true) {
            try(Socket socket=ss.accept()) {
                // 先读取请求类型
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String requestType = br.readLine();
                if (requestType == null) return;
                switch (requestType) {
                    case "ACCOUNT_UPLOAD":
                        System.out.println("=============");
                        System.out.println("收到上传账务请求");
                        uploadAccount(socket, br); // 使用字符流处理账务上传
                        System.out.println("=============");
                        break;
                    case "ACCOUNT_DOWNLOAD":
                        System.out.println("=============");
                        System.out.println("收到下载账务请求");
                        downloadAccount(socket, br); // 使用字符流
                        System.out.println("=============");
                        break;
                    case "FILE_UPLOAD":
                        System.out.println("=============");
                        System.out.println("收到上传文件请求");
                        uploadFile(socket,is);
                        System.out.println("=============");
                        break;
                    case "FILE_DOWNLOAD":
                        System.out.println("=============");
                        System.out.println("收到下载文件请求");
                        downloadFile(socket,br);
                        System.out.println("=============");
                        break;

                    default:
                        System.out.println("未知请求类型: " + requestType);
                }
            } catch (IOException e) {
                System.err.println("客户端处理错误: " + e.getMessage());
            }
        }
    }


    //上传账务
    private static void uploadAccount(Socket socket, BufferedReader br) {
        String username=null;
        String filename=null;
        //接收参数
        String line=null;
        try {
            while((line=br.readLine())!=null){
                username=line.substring(0,line.indexOf("/"));
                filename=line.substring(line.indexOf("/")+1);

                //创建目录
                File accountDir = new File(username + "\\");
                if(!accountDir.exists()){
                    boolean mkdir = accountDir.mkdir();
                    System.out.println( mkdir);
                }


                //读取内容写入文件
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(accountDir.getPath() + "\\"+filename));
                    while(!"END".equals(line=br.readLine())){
                        bw.write(line);
                        bw.newLine();
                    }
                    bw.close();

                    System.out.println(filename+"文件上传成功");
                    uploadAccounts.add(filename);
                    new PrintWriter(socket.getOutputStream(), true).println("SUCCESS");//给客户端反馈
                } catch (IOException e) {
                    System.out.println(filename+"文件上传失败");
                    new PrintWriter(socket.getOutputStream()).println("FAILD");//给客户端反馈
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.err.println("客户端异常"+e.getMessage());
        }
        finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




    //下载账务
    private static void downloadAccount(Socket socket,BufferedReader br) {
        //获取用户名
        String username= null;
        try {
            username = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File dir = new File(username);
        if(!dir.exists()){
            System.out.println("该用户还未上传账务");
            return;
        }
        //返回文件名
        String filenames="";
        ArrayList<String> uploadAccounts1= new ArrayList<>(uploadAccounts);


        for(int i=0;i<uploadAccounts1.size();i++){
            filenames+=(i+1)+"."+uploadAccounts1.get(i)+" ";
        }

        try(BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));){
            //传输已经上传过的文件集
            if (filenames != null) {
                bw.write(filenames);
            }else{
                bw.write("");
            }
            bw.newLine();
            bw.flush();

            //接收请求下载文件的索引
            String[] split = br.readLine().split(",");

            //下载文件
            for(String str:split){
                int index = Integer.parseInt(str.trim());
                //处理文件索引越界
                if(index<0||index>uploadAccounts.size()){
                    System.out.println("文件"+index+"不存在");
                    continue;
                }

                //获取文件
                String filename = uploadAccounts1.get(index-1);
                File file = new File(username + "\\" + filename);
                //传输文件名
                bw.write(filename);
                bw.newLine();
                bw.flush();

                //传输文件
                try(BufferedReader fileReader = new BufferedReader(new FileReader(file));){
                    String line=null;
                    while((line=fileReader.readLine())!=null){
                        bw.write( line);
                        bw.newLine();
                    }
                    bw.flush();

                    //文件结束标志
                    bw.write("END");
                    bw.newLine();
                    System.out.println("文件"+index+"传输成功");
                }catch (IOException e){
                    System.out.println("文件"+index+"传输失败");
                    throw new RuntimeException(e);
                }

            }
        }catch (IOException e){
            System.out.println("文件传输失败");
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 安全读取行（替代BufferedReader）
    private static String readLineFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1 && b != '\n') {
            if (b != '\r') {  // 处理Windows换行
                baos.write(b);
            }
        }
        return baos.toString("GBK");
    }

    private static String[] parseMetaData(String line) {
        String[] parts = line.split("\\|", 2);  // 最多分割2部分
        if (parts.length != 2) {
            throw new IllegalArgumentException("格式错误，应为 username|filename");
        }
        return parts;
    }
    private static void uploadFile(Socket socket, InputStream is) {
        try {
            String metaLine = readLineFromStream(is);
            String[] parts = parseMetaData(metaLine);  // 安全解析
            String username = parts[0];
            String filename = parts[1];

            //创建目录
            File dir = new File(username);
            if(!dir.exists()){
                dir.mkdir();
            }

            //创建文件
            File file = new File(dir, filename);
            BufferedInputStream bis=new BufferedInputStream(is);
            try(BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));){
                byte[] buffer=new byte[8192];
                int len;
                while((len=bis.read(buffer))!=-1){
                    bos.write(buffer,0,len);
                }
                System.out.println(file.getName()+"文件上传成功");
            }catch (IOException e){
                System.out.println(file.getName()+"文件上传失败");
                e.printStackTrace();
            }} catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void downloadFile(Socket socket,BufferedReader br)  {
        try(BufferedOutputStream bos=new BufferedOutputStream(socket.getOutputStream());) {

            //接收用户名
            String username = br.readLine();
            if(username==null)
                return;
            //创建目录
            File dir = new File(username);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String filelist = "";

            File[] files = dir.listFiles();
            //传输文件列表
            int index=1;
           ArrayList<String> uploadedFiles = new ArrayList<>();
            for (File file : files) {
                if(!uploadAccounts.contains(file.getName())) {
                    filelist += (index++) + "." + file.getName() + " ";
                    uploadedFiles.add(file.getName());
                }
            }

            PrintWriter pw=new PrintWriter(bos,true);
            pw.println(filelist);
            System.out.println(filelist);
            //接收请求下载文件序号
            Integer i = Integer.parseInt(br.readLine());
            System.out.println("接收序号：" + i);

            //检查序号是否合法
            if (i < 0 || i > uploadedFiles.size()) {
                System.out.println("该文件不存在");
                pw.println("不存在");
                return;
            }
            //传输文件名
            pw.println(uploadedFiles.get(i-1));

            System.out.println("文件名称"+uploadedFiles.get(i-1));
            BufferedInputStream fileInput = new BufferedInputStream(new FileInputStream(new File(dir,uploadedFiles.get(i-1))));
            //合法：传输该文件
            byte[] buffer = new byte[1024];
            int len=0;
            while ((len =fileInput.read(buffer))!=-1){
                bos.write(buffer,0,len);
            }
            bos.flush();
            socket.shutdownOutput();
            System.out.println(uploadedFiles.get(i-1)+"文件下载成功");
        }catch (IOException e){
            System.out.println("文件下载失败");
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

