//-*- coding =utf-8 -*-
//@Time : 2023/8/11
//@Author: 邓闽川
//@File  FilePath.java
//@software:IntelliJ IDEA
package me.devedmc.streamq.commitlog;

public class FilePath {
    public static final String FILE_INDEX_CONF_PATH=System.getProperty("file.separator")+"fileIndex.conf";
    public static final String COMMITLOG_OFFSET=System.getProperty("file.separator")+"commitlogOffset.conf";

    public static final String MESSAGE_STORAGE_FILE_PATH=System.getProperty("file.separator");
    public static final String MANAGER_FILE_PATH=System.getProperty("file.separator")+"managerFile.conf";

}
