//-*- coding =utf-8 -*-
//@Time : 2023/8/26
//@Author: 邓闽川
//@File  VitualBroker.java
//@software:IntelliJ IDEA
package me.deve.streamq.client.loadbalance;

import lombok.Getter;
import me.deve.streamq.common.component.Broker;

public class VirtualBroker {
    @Getter
    private Broker broker;

    private Integer virtual_id;

    public VirtualBroker(Broker broker, int i) {
        this.broker=broker;
        this.virtual_id=i;
    }
}
