//-*- coding =utf-8 -*-
//@Time : 2023/6/23
//@Author: 邓闽川
//@File  KVManager.java
//@software:IntelliJ IDEA
package me.deve.streamq.nameserver.manager;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.constant.FileConstant;
import me.deve.streamq.common.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
@Slf4j
public class KVManager {

    private final ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
    /**
     * store living brokers
     */
    ConcurrentHashMap<Broker, DateTime> livingBrokers;
    public KVManager(ConcurrentHashMap<Broker, DateTime> livingBrokers){
        this.livingBrokers=livingBrokers;
    }

    /**
     * persist map data to file
     */
    public void persist() {
        /**
         * add read-lock because of high frequency read operations
         */
        File newFile = new File(FileConstant.NEW_BROKER_NAME);
        File file=new File(FileConstant.BROKER_NAME);
        try {
                boolean successCreateFile = newFile.createNewFile();
                if(!successCreateFile){
                    log.error("create file failed");
                }
                String jsonStr = JSONUtil.toJsonStr(livingBrokers);
                FileUtil.string2File(newFile,jsonStr,false);
                this.readWriteLock.writeLock().lockInterruptibly();
                FileUtil.replace(file,newFile);
        } catch (InterruptedException e) {
            log.error("read-lock error,e:"+e.getMessage());
        } catch (IOException e) {
            log.error("io error,e:"+e.getMessage());
        } finally {
            this.readWriteLock.writeLock().unlock();
        }

    }

    /**
     * load data from file
     */
    public void load(){

    }
}
