//-*- coding =utf-8 -*-
//@Time : 2023/6/16
//@Author: 邓闽川
//@File  SendStatus.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

public enum SendStatus {
    /**
     * 发送成功
     */
    SEND_OK,
    FLUSH_DISK_TIMEOUT,
    FLUSH_SLAVE_TIMEOUT,
    SLAVE_NOT_AVAILABLE,
}
