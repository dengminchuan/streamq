//-*- coding =utf-8 -*-
//@Time : 2023/7/28
//@Author: 邓闽川
//@File  MessageInfo.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;

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
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.putLong(queueOffset);
        buffer.putLong(length);
        buffer.putInt(tagHashCode);
        return buffer.array();
    }
    public  static MessageInfo fromByteArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long offset = buffer.getLong();
        long length = buffer.getLong();
        int tagHashCode = buffer.getInt();
        return new MessageInfo(offset, length, tagHashCode);
    }



}
