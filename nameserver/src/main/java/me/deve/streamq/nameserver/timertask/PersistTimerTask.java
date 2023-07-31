//-*- coding =utf-8 -*-
//@Time : 2023/6/25
//@Author: 邓闽川
//@File  PersistTimerTask.java
//@software:IntelliJ IDEA
package me.deve.streamq.nameserver.timertask;

import cn.hutool.core.date.DateTime;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.constant.TimerConstant;
import me.deve.streamq.common.util.FileUtil;
import me.deve.streamq.nameserver.manager.KVManager;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Timed Persistence Map
 */
@Slf4j
public class PersistTimerTask extends TimerTask {
    private final ConcurrentHashMap<Broker, DateTime> livingBrokers;
    private final Timer timer=new Timer();

    private KVManager kvManager;
    public PersistTimerTask(ConcurrentHashMap<Broker,DateTime> livingBrokers){
        this.livingBrokers=livingBrokers;
        kvManager=new KVManager(livingBrokers);
    }
    @Override
    public void run() {
         kvManager.persist();
    }
    public void start(){
        timer.schedule(this, TimerConstant.PERSIST_TIMER_DELAY,TimerConstant.PERSIST_TIMER_PERIOD);
    }

}
