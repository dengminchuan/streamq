//-*- coding =utf-8 -*-
//@Time : 2023/7/28
//@Author: 邓闽川
//@File  MessageInfo.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

/**
 * record message info
 */
public class MessageInfo {
    /**
     * 8 byte
     */
    private Long queueOffset;
    /**
     * 8 byte
     */
    private Long length;
    /**
     * 4byte
     */
    private Integer tagHashCode;
}
