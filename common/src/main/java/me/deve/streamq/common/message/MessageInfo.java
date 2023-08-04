//-*- coding =utf-8 -*-
//@Time : 2023/7/28
//@Author: 邓闽川
//@File  MessageInfo.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * record message info
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public MessageInfo(Long queueOffset, Long length){
        this.queueOffset=queueOffset;
        this.length=length;
    }
}
