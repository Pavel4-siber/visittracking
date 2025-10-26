package com.example.visittracking.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.example.visittracking.util.TimestampConverter.getDetailedTimeInfo;

/**
 * @author Pavel Zhurenkov
 */
@Component
public class UserActionEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserActionEventListener.class);

    @EventListener
    public void handleUserActionEvent(UserActionEvent event) {
        logger.info("User action occurred: description={}, timestamp={}",
                event.getDescription(), getDetailedTimeInfo(event.getTimestamp()));

        //todo
    }
}

