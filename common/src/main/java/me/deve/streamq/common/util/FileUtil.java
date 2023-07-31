//-*- coding =utf-8 -*-
//@Time : 2023/6/24
//@Author: 邓闽川
//@File  FileUtil.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FileUtil {
    public static void string2File(File file, String str,boolean append) {
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                log.error("file error"+e.getMessage());
            }
        }
        BufferedWriter writer=null;
        try {
            writer = new BufferedWriter(new FileWriter(file,append));
            writer.write(str);
        } catch (IOException e) {
            log.error("file error"+e.getMessage());
        }finally {
            try {
                if(writer!=null) writer.close();
            } catch (IOException e) {
                log.error("file error"+e.getMessage());
            }
        }
    }
    public static String file2String(File file){
        if(!file.exists()){
            log.error("file do not exist");
        }
        BufferedReader reader=null;
        StringBuffer content = new StringBuffer();
        try {
            reader=new BufferedReader(new FileReader(file));
            String line=null;
            while ((line=reader.readLine())!=null){
                content.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if(reader!=null)reader.close();
            } catch (IOException e) {
                log.error("file error"+e.getMessage());
            }
        }

        return content.toString();
    }
    public static void write2Binary(File file,byte[] content,boolean append) {
        // 创建二进制输出流
        try ( FileOutputStream  fos = new FileOutputStream(file,append)){
            fos.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





    /**
     * delete source and rename target to source
     * @param source
     * @param target
     */
    public static boolean replace(File source,File target) {
       if(source.exists()){
           boolean delete = source.delete();
           if(!delete){
               return false;
           }
       }
       if(!target.exists()){
           try {
               boolean newFile = target.createNewFile();
               if(!newFile)return false;
           } catch (IOException e) {
               throw new RuntimeException(e);
           }
       }
        return target.renameTo(source);


    }
}
