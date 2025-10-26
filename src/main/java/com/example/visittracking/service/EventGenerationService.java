package com.example.visittracking.service;

import com.example.visittracking.listener.UserActionEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


/**
 * @author Pavel Zhurenkov
 */
@Service
public class EventGenerationService {

    private final ApplicationEventPublisher eventPublisher;


    public EventGenerationService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void createVisitEvent(){
        UserActionEvent event = new UserActionEvent(
                this,
                "Created visits successfully"
        );
        eventPublisher.publishEvent(event);
    }

    public void getPatientVisitsEvent(){
        UserActionEvent event = new UserActionEvent(
                this,
                "Get patients visits successfully"
        );
        eventPublisher.publishEvent(event);
    }
}

