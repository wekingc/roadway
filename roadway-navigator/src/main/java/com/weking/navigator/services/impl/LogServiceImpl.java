package com.weking.navigator.services.impl;

import com.weking.core.services.interfaces.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Jim Cen
 * @date 2020/7/7 13:46
 */
@Service("logService")
@Slf4j
public class LogServiceImpl implements LogService {
    @Override
    public boolean log(Level level,String message) {
        switch (level) {
            case INFO: log.info(message);break;
            case WARN: log.warn(message);break;
            case DEBUG: log.debug(message);break;
            case ERROR: log.error(message);break;
            case TRACE: log.trace(message);break;
            default:
        }
        return true;
    }
}
