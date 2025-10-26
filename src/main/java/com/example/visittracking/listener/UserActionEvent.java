package com.example.visittracking.listener;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Pavel Zhurenkov
 */
@Getter
public class UserActionEvent extends ApplicationEvent {

    private final String description;

    public UserActionEvent(Object source, String description) {
        super(source);
        this.description = description;
    }

}
