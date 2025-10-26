package com.example.visittracking.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.RequestHandledEvent;

/**
 * @author Pavel Zhurenkov
 */
@Component
public class UserEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserEventListener.class);

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Context refreshed");
    }

    @EventListener
    public void handleApplicationStarted(ApplicationStartedEvent event) {
        logger.info("Application started");
    }

    @EventListener
    public void onApplicationEvent(RequestHandledEvent event) {
        logger.info("Request handled: : description={}", event.getDescription());
    }
}

