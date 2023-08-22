//-*- coding =utf-8 -*-
//@Time : 2023/8/21
//@Author: 邓闽川
//@File  IdWoker.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.util;

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
    /**
     * highest 11:not used
     * middle 41:timestamp
     * lowest 12:sequence
     */
    private AtomicLong timeStampAndSequence;
    /**
     * -1 left shift 53bit then get ~,  timeStampAndSequence&timeStampAndSequenceMask=lowest 53bits
     */
    private final long timeStampAndSequenceMask=~(-1L<<(workIdBits+sequenceBits));

    public IdWorker(long workId){
           initTimeStampAndSequence();
           initWorkId(workId);
    }

    private void initWorkId(long workId) {

    }

    private void initTimeStampAndSequence() {

    }


    private long getNewestTimeStamp(){
        return System.currentTimeMillis()-startTime;
    }


       public  long nextId(){
            return 0;
       }
}
