//-*- coding =utf-8 -*-
//@Time : 2023/8/22
//@Author: 邓闽川
//@File  IdUtil.java
//@software:IntelliJ IDEA
package me.deve.streamq.common.util;

import lombok.extern.slf4j.Slf4j;
import me.deve.streamq.common.annotation.PreferId;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
@Slf4j
public class IdDistributor {
       private static volatile IdDistributor instance;

       private final IdWorker idWorker;
       private IdDistributor(){
           idWorker=new IdWorker();
       }
       public static IdDistributor getInstance(){
           if(instance==null){
               synchronized(IdDistributor.class){
                   if(instance==null){
                        instance=new IdDistributor();
                   }
               }
           }
           return instance;
       }
       public long getId(){
           return idWorker.nextId();
       }
       public boolean setIdByAnnotation(Object object){
           Class clazz = object.getClass();
           Field[] allFields = clazz.getDeclaredFields();
           for (Field field : allFields) {
               field.setAccessible(true);
               boolean needSetId = field.isAnnotationPresent(PreferId.class );
               if(needSetId){
                   try {
                       if(field.get(object)!=null){
                           return false;
                       }
                       PreferId preferId = field.getAnnotation(PreferId.class);
                       String s = preferId.workId();
                       int maxIncrementCnt = preferId.maxIncrementCnt();
                       synchronized (idWorker) {
                           if(StringUtils.isNotBlank(s)){
                               idWorker.setWorkId(Long.parseLong(s));
                           }
                           if(maxIncrementCnt!=-1){
                               idWorker.setMaxIncrementCnt(maxIncrementCnt);
                           }
                       }
                       field.set(object,  idWorker.nextId());
                   } catch (IllegalAccessException e) {
                       log.error(String.valueOf(e));
                       return false;
                   }
               }
           }
           return true;
       }


}
