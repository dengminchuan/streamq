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

import static me.devedmc.streamq.commitlog.FilePath.*;


@Slf4j
public class CommitLog {
    private String location=System.getProperty("user.dir");;


    private Integer previousUsingFileIndex=0;

    private File currentUsingFile;

    private FlushDiskType flushDiskType=FlushDiskType.ASYN_FLUSH_DISK;

    private volatile ArrayList<File> files=new ArrayList<>();

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
        KryoSerializer kryoSerializer = new KryoSerializer();
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(() -> {
            //record current file index
            File file = new File(location + FILE_INDEX_CONF_PATH);
            FileUtil.string2File(file,currentFileIndex.toString(),false);
            flushDiskService.shutdown();
            persist();
            File offsetFile = new File(location + COMMITLOG_OFFSET);
            FileUtil.string2File(offsetFile, commitLogOffset.toString(),false);
            System.out.println(files.size());
            return null;
        }));
        initFiles();
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

    private void initFiles() {
        File folder = new File(location);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().matches("\\d{20}\\.bin")) {
                this.files.add(file);
            }

        }

    }


    private void updateMessageFile(Integer fileIndex){
        String messageFileName = String.format("%020d", fileIndex * MAX_SINGLE_FILE_SIZE)+".bin";
        File messageFile = new File(location + MESSAGE_STORAGE_FILE_PATH+messageFileName);
        if(!messageFile.exists()){
            try {
                messageFile.createNewFile();
                files.add(messageFile);
            } catch (IOException e) {
                log.error("create file error");
            }
        }
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
    public Message readMessage(Long offset, Long length){
        log.info("commit log read offset:{}",offset);
        int fileIndex = judgeIndex(offset);
        if(fileIndex>currentFileIndex){
            return null;
        }
        File currentUsingFile = this.files.get(fileIndex);
        String fileName = currentUsingFile.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        long messageOffset = offset - Long.parseLong(fileName);
        return parse2Message(currentUsingFile,messageOffset, length);

    }
    private Message parse2Message(File currentUsingFile, long messageOffset, Long length) {
        byte[] bytes = new byte[Math.toIntExact(length)];
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(currentUsingFile,"r");){
            randomAccessFile.seek(messageOffset);
            randomAccessFile.read(bytes,0, Math.toIntExact(length));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        KryoSerializer messageSerializer = new KryoSerializer();
        return messageSerializer.deserialize(bytes, Message.class);
    }

    private int judgeIndex(Long offset) {
        int index=0;
        int totalCount=0;
        for(int i=0;i<files.size();i++){
            if(totalCount<=offset&&totalCount+files.get(i).length()>offset){
                break;
            }else{
                index++;
                totalCount+= (int) files.get(i).length();
            }
        }


        return index;
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
                    files.set(currentFileIndex,currentUsingFile);
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
