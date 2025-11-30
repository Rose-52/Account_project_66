package com.cuit.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FileWriterUtil {
    public static void writeFile(String path, HashMap<Date, List<String>> content) throws IOException {
        Set<Date> list_content = content.keySet();
        //遍历日期
        for(Date date : list_content) {
            String filepath = path+date.toString()+".txt";
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
                //遍历某一日期的内容
                for (String line : content.get(date)) {
                    bw.write(line);
                    bw.newLine();
                }
            } catch (IOException e) {
                throw new RuntimeException("写入文件失败"+e);
            }
        }
    }
}
