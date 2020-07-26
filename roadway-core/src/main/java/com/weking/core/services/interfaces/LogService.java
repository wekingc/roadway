package com.weking.core.services.interfaces;

/**
 * @author Jim Cen
 * @date 2020/7/7 13:43
 * 日志记录接口
 */
public interface LogService {
    enum Level {
        //warn等级
        WARN,
        //info等级
        INFO,
        //debug等级
        DEBUG,
        //error等级
        ERROR,
        //trace等级
        TRACE
    }
    /**
     * 记录信息
     * @param level 记录等级
     * @param message 信息
     * @return 是否成功
     */
    boolean log(Level level,String message);
}