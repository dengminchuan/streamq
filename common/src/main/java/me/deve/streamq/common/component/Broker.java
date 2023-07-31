//-*- coding =utf-8 -*-
//@Time : 2023/6/23
//@Author: 邓闽川
//@File  Broker.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.deve.streamq.common.address.KryoInetAddress;
@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * broker basic info
 */
public class Broker {
    private Long id;
    private String name;

    private KryoInetAddress inetAddress;
    /**
     * which topic that the broker belong
     */
    private String topicName;
}
