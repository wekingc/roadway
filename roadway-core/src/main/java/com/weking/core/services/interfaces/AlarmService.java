package com.weking.core.services.interfaces;

/**
 * @author Jim Cen
 * @date 2020/7/21 13:03
 */
public interface AlarmService {
    /**
     * 警告服务
     * @param subject 主题
     * @param body 内容
     * @param html 是否html格式
     */
    void alarm(String subject,String body,boolean html);

    /**
     * 默认非HTML格式
     * @param subject  主题
     * @param body 内容
     */
    default void alarm(String subject, String body) {
        alarm(subject,body,false);
    }
}
