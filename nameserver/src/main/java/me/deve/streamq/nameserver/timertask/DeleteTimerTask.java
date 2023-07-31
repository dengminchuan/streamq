//-*- coding =utf-8 -*-
//@Time : 2023/6/25
//@Author: 邓闽川
//@File  DeleteTimerTask.java
//@software:IntelliJ IDEA
package me.deve.streamq.nameserver.timertask;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.constant.TimerConstant;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * delete brokers when does not accept heartbeat
 */
public class DeleteTimerTask extends TimerTask {
    private final ConcurrentHashMap< Broker,DateTime> livingBrokers;

    private final Timer timer=new Timer();

    public DeleteTimerTask(ConcurrentHashMap<Broker,DateTime> livingBrokers) {
        this.livingBrokers=livingBrokers;

    }

    /**
     * check livingBrokers
     */
    @Override
    public void run() {
       checkAndDelete();
    }
    public void start(){
        timer.schedule(this, TimerConstant.PERSIST_TIMER_DELAY,TimerConstant.DELETE_TIMER_PERIOD);
    }
    public void checkAndDelete() {
        Iterator<Map.Entry<Broker,DateTime>> iterator = livingBrokers.entrySet().iterator();
        DateTime date=null;
        System.out.println(livingBrokers.size());
        while(iterator.hasNext()){
            Map.Entry<Broker,DateTime> dateBroker = iterator.next();
            date=DateUtil.date();
            long l = DateUtil.betweenMs(dateBroker.getValue(), date);
            if(l>TimerConstant.HEART_BEAT_ELIMINATE_TIME){
                iterator.remove();
            }
        }
    }
}
