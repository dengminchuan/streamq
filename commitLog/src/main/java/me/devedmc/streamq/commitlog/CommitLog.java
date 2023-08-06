//-*- coding =utf-8 -*-
//@Time : 2023/7/29
//@Author: 邓闽川
//@File  CommitLog.java
//@software:IntelliJ IDEA
package me.devedmc.streamq.commitlog;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.thread.ShutdownHookThread;
import me.deve.streamq.common.util.FileUtil;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


@Slf4j
public class CommitLog {
    private String location=System.getProperty("user.dir");;


    private final String FILE_INDEX_CONF_PATH=System.getProperty("file.separator")+"fileIndex.conf";
    private final String COMMITLOG_OFFSET=System.getProperty("file.separator")+"commitlogOffset.conf";

    private final String MESSAGE_STORAGE_FILE_PATH=System.getProperty("file.separator");


    private Integer previousUsingFileIndex=0;

    private  List<File> storeFiles=new ArrayList<>();
    private File currentUsingFile;

    private FlushDiskType flushDiskType=FlushDiskType.ASYN_FLUSH_DISK;


    @Getter
    private Long commitLogOffset =0L;

    /**
     * single file max 1GB
     */
    private final Long MAX_SINGLE_FILE_SIZE = 1024*1024*1024L;
    /**
     * current use file
     */
    private Integer currentFileIndex=0;
    private static volatile CommitLog commitLog;

    private Boolean isAdd=false;

    private final ConcurrentLinkedQueue<Message> pageCache=new ConcurrentLinkedQueue<>();
    private ScheduledExecutorService flushDiskService = Executors.newScheduledThreadPool(1);
    private CommitLog(){
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(() -> {
            //record current file index
            File file = new File(location + FILE_INDEX_CONF_PATH);
            FileUtil.string2File(file,currentFileIndex.toString(),false);
            flushDiskService.shutdown();
            persist();
            File offsetFile = new File(location + COMMITLOG_OFFSET);
            FileUtil.string2File(offsetFile, commitLogOffset.toString(),false);
            return null;
        }));
        File indexFile = new File(location + FILE_INDEX_CONF_PATH);
        if(indexFile.exists()){
            previousUsingFileIndex = Integer.parseInt(FileUtil.file2String(indexFile));
        }else{
            previousUsingFileIndex=0;
        }
        updateMessageFile(previousUsingFileIndex);
        File offsetFile = new File(location + COMMITLOG_OFFSET);
        if(offsetFile.exists()){
            commitLogOffset= Long.valueOf(FileUtil.file2String(new File(location + COMMITLOG_OFFSET)));
        }else{
            commitLogOffset=0L;
        }

    }



    private void updateMessageFile(Integer fileIndex){
        String messageFileName = String.format("%020d", fileIndex * MAX_SINGLE_FILE_SIZE)+".bin";
        File messageFile = new File(location + MESSAGE_STORAGE_FILE_PATH+messageFileName);
        if(!messageFile.exists()){
            try {
                messageFile.createNewFile();
            } catch (IOException e) {
                log.error("create file error");
            }
        }
        storeFiles.add(messageFile);
        currentUsingFile=messageFile;

    }
    public static CommitLog getInstance() {
        if(commitLog==null){
            synchronized(CommitLog.class){
                if(commitLog==null){
                    commitLog=new CommitLog();
                }
            }
        }
        return commitLog;
    }

    /**
     *
     * @param message
     * @return offset
     */
    public Long add(Message message){
        if(!isAdd){
            isAdd=true;
            if(flushDiskType==FlushDiskType.ASYN_FLUSH_DISK){
                flushDiskAsyn();
            }
        }
        switch (flushDiskType){
            case ASYN_FLUSH_DISK -> {
                int length = getLength(message);
                commitLogOffset +=length;
                pageCache.add(message);
                return commitLogOffset-length;
            }
            case SYN_FLUSH_DISK -> {
                return 0L;
            }
        }
        return -1L;
    }
    public static int getLength(Message message) {
        KryoSerializer kryoSerializer = new KryoSerializer();
        byte[] messageBytes = kryoSerializer.serialize(message);
        return messageBytes.length;
    }
    public void flushDiskAsyn(){
        flushDiskService = Executors.newScheduledThreadPool(1);
        Runnable task = this::persist;
        flushDiskService.scheduleWithFixedDelay(task,FlushConstant.ASYN_FLUSH_DISK_INITIAL_DELAY,FlushConstant.ASYN_FLUSH_DISK_DELAY, TimeUnit.MILLISECONDS);
    }

    private void persist() {
        if(!pageCache.isEmpty()){
            Message message;
            while((message=pageCache.poll())!=null){
                KryoSerializer kryoSerializer = new KryoSerializer();
                byte[] messageBytes = kryoSerializer.serialize(message);
                int length=messageBytes.length;
                if(length+ currentUsingFile.length() >= MAX_SINGLE_FILE_SIZE){
                    currentFileIndex++;
                    updateMessageFile(currentFileIndex);
                }
                FileUtil.write2Binary(currentUsingFile,messageBytes,true);
            }
        }
    }

    public static byte[] combineBytes(byte[] bytes1,byte[] bytes2){
        byte[] bytes=new byte[bytes1.length+bytes2.length];
        for(int i=0;i<bytes.length;i++){
            if(i<bytes1.length){
                bytes[i]=bytes1[i];
            }else{
                bytes[i]=bytes2[i-bytes1.length];
            }
        }
        return bytes;
    }

    public void setFlushDiskType(FlushDiskType flushDiskType) {
        this.flushDiskType = flushDiskType;
    }
}
