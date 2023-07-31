//-*- coding =utf-8 -*-
//@Time : 2023/6/27
//@Author: 邓闽川
//@File  TopicRouteData.java
//@software:IntelliJ IDEA
package me.deve.streamq.remoting.route;

import me.deve.streamq.common.component.Broker;
import me.deve.streamq.common.component.Topic;

import java.util.ArrayList;
import java.util.List;

public class TopicRouteData {
    private final String topicName;

    private final List<Broker> brokers=new ArrayList<>();

    public TopicRouteData(String topicName){
        this.topicName=topicName;
    }

}
