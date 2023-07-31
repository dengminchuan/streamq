//-*- coding =utf-8 -*-
//@Time : 2023/6/23
//@Author: 邓闽川
//@File  Topic.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.component;

import java.util.List;

public class Topic {
    /**
     * globally unique id
     */
    private Long id;
    /**
     * globally unique name
     */
    private String name;

    private List<Broker> brokers;
}
