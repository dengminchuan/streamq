//-*- coding =utf-8 -*-
//@Time : 2023/8/21
//@Author: 邓闽川
//@File  IdWoker.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.util;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.RandomUtil;

import java.util.concurrent.atomic.AtomicLong;

public class IdWorker {
    /**
     * 2023-3-1
     */
    private final long startTime=1677600000000L;

    private final int workIdBits=10;

    private final int timeStampBits=41;

    private final int sequenceBits=12;

    private final int maxWorkerId=1023;

    public void setMaxIncrementCnt(int maxIncrementCnt) {
        this.maxIncrementCnt = maxIncrementCnt;
    }

    private int maxIncrementCnt=2;

    public void setWorkId(long workId) {
        this.workId = workId;
    }

    private long workId;

    /**
     * highest 11:not used
     * middle 41:timestamp
     * lowest 12:sequence
     */
    private AtomicLong timeStampAndSequence;
    /**
     * -1 left shift 53bit then get ~,  timeStampAndSequence&timeStampAndSequenceMask=lowest 53bits
     */
    private final long timeStampAndSequenceMask=~(-1L<<(timeStampBits+sequenceBits));

    public IdWorker(long workId){
           initTimeStampAndSequence();
           initWorkId(workId);
    }
    public IdWorker(){
           initTimeStampAndSequence();
           initWorkId(null);
    }

    private void initWorkId(Long workId) {
            if(workId==null){
                workId=generateWorkId();
            }else{
                if(workId<0||workId>maxWorkerId){
                    throw new IllegalArgumentException("please set a right work id,less than 1024 and greater than -1");
                }

            }
        this.workId=workId<<(sequenceBits+timeStampBits);
    }

    private long generateWorkId() {
        try {
            return (Integer.parseInt(NetUtil.getLocalMacAddress())) %(maxWorkerId+1);
        } catch (Exception e) {
            return RandomUtil.randomInt(0,maxWorkerId+1);
        }
    }

    private void initTimeStampAndSequence() {
            long timeStamp=getNewestTimeStamp();
            long timeStampWithSequence = timeStamp << sequenceBits;
            this.timeStampAndSequence=new AtomicLong(timeStampWithSequence);
    }


    private long getNewestTimeStamp(){
        return System.currentTimeMillis()-startTime;
    }


       public  long nextId() {
            int incrementCnt=RandomUtil.randomInt(1,maxIncrementCnt);
            long next=0;
            for(int i=0;i<=incrementCnt;i++){
                next=timeStampAndSequence.incrementAndGet();
            }
            long nextTimeStampAndSequence = next & timeStampAndSequenceMask;
            return workId|nextTimeStampAndSequence;
       }
    /**
     * more safe but will reduce efficiency
     * @return id
     */
    public long nextIdWithCheck(){
           waitIfNecessary();
           return nextId();
       }


    private void waitIfNecessary() {
        long currentWithSequence = timeStampAndSequence.get();
        long current=currentWithSequence>>sequenceBits;
        long newest=getNewestTimeStamp();
        if(newest<=current){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                //don‘t care
            }
        }

    }
}
