package me.devedmc.streamq.commitlog;

import me.deve.streamq.common.message.Message;


public class CommitLogApplication {

    public static void main(String[] args) {
            CommitLog instance = CommitLog.getInstance();
            instance.add(new Message("i","123".getBytes()));
    }

}
