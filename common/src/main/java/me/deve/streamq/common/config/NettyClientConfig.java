//-*- coding =utf-8 -*-
//@Time : 2023/6/17
//@Author: 邓闽川
//@File  NettyClientConfig.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.deve.streamq.common.address.KryoInetAddress;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NettyClientConfig {

    private KryoInetAddress inetAddress;


}
