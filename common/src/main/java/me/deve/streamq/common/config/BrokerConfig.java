//-*- coding =utf-8 -*-
//@Time : 2023/6/19
//@Author: 邓闽川
//@File  BrokerConfig.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.config;

import cn.hutool.core.util.NumberUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrokerConfig {
    private Long id= System.currentTimeMillis();
    private String name="broker index:"+id;
}
