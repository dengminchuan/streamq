//-*- coding =utf-8 -*-
//@Time : 2023/7/29
//@Author: 邓闽川
//@File  CommitLog.java
//@software:IntelliJ IDEA
package me.devedmc.streamq.commitlog;

import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.message.Message;
import me.deve.streamq.common.thread.ShutdownHookThread;
import me.deve.streamq.common.util.FileUtil;
import me.deve.streamq.common.util.serializer.KryoSerializer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Slf4j
public class CommitLog {
    private String location;


    private final String FILE_INDEX_CONF_PATH=System.getProperty("file.separator")+"fileIndex.conf";

    private final String MESSAGE_STORAGE_FILE_PATH=System.getProperty("file.separator");

    private final String FILE_PATH=System.getProperty("file.separator")+"commitLog";

    private Integer previousUsingFileIndex=0;

    private final List<File> storeFiles =new ArrayList<>();
    private File currentUsingFile;

    private FlushDiskType flushDiskType=FlushDiskType.ASYN_FLUSH_DISK;

    private ConcurrentHashMap<String,Long> offsetMap=new ConcurrentHashMap<>();

    private Long commitLogOffset =0L;

    /**
     * single file max 1GB
     */
    private Long offset = 60L;
    /**
     * current use file
     */
    private Integer currentFileIndex=0;
    private static volatile CommitLog commitLog;

    private Boolean isAdd=false;

    private final ConcurrentLinkedQueue<Message> pageCache=new ConcurrentLinkedQueue<>();
    private CommitLog(){
        location=System.getProperty("user.dir")+FILE_PATH;
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(() -> {
            //record current file index
            File file = new File(location + FILE_INDEX_CONF_PATH);
            FileUtil.string2File(file,currentFileIndex.toString(),false);
            return null;
        }));
        File indexFile = new File(location + FILE_INDEX_CONF_PATH);
        previousUsingFileIndex = Integer.parseInt(FileUtil.file2String(indexFile));
        updateMessageFile(previousUsingFileIndex);
    }
    private void updateMessageFile(Integer fileIndex){
        String messageFileName = String.format("%020d", fileIndex * offset)+".bin";
        File messageFile = new File(location + MESSAGE_STORAGE_FILE_PATH+messageFileName);
        if(!messageFile.exists()){
            try {
                messageFile.createNewFile();
            } catch (IOException e) {
                log.error("create file error");
            }
        }
        storeFiles.add(currentUsingFile);
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
    public void add(Message message){
        if(!isAdd){
            isAdd=true;
            if(flushDiskType==FlushDiskType.ASYN_FLUSH_DISK){
                flushDiskAsyn();
            }
        }
        switch (flushDiskType){
            case ASYN_FLUSH_DISK -> {
                pageCache.add(message);
            }
            case SYN_FLUSH_DISK -> {

            }
        }

    }
    public void flushDiskAsyn(){
        ScheduledExecutorService flushDiskService = Executors.newScheduledThreadPool(1);
        Runnable task = () -> {
            if(!pageCache.isEmpty()){
                pageCache.forEach(message -> {
                    KryoSerializer kryoSerializer = new KryoSerializer();
                    byte[] messageBytes = kryoSerializer.serialize(message);
                    int length=messageBytes.length;
                    if(length+ currentUsingFile.length() >=offset){
                        currentFileIndex++;
                        updateMessageFile(currentFileIndex);
                    }
                    commitLogOffset +=length;
                    FileUtil.write2Binary(currentUsingFile,messageBytes,true);
                    offsetMap.put(message.getId(),commitLogOffset);
                });
                pageCache.clear();
            }
        };
        flushDiskService.scheduleWithFixedDelay(task,FlushConstant.ASYN_FLUSH_DISK_INITIAL_DELAY,FlushConstant.ASYN_FLUSH_DISK_DELAY, TimeUnit.MILLISECONDS);
    }
    public Long getAndRemoveMessageOffset(String id){
        Long offset = offsetMap.get(id);
        offsetMap.remove(id);
        return offset;
    }

    public void setFlushDiskType(FlushDiskType flushDiskType) {
        this.flushDiskType = flushDiskType;
    }
}
