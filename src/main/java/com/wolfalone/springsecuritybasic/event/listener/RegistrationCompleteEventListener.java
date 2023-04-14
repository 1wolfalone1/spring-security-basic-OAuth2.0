package com.wolfalone.springsecuritybasic.event.listener;

import com.wolfalone.springsecuritybasic.entity.User;
import com.wolfalone.springsecuritybasic.event.RegistrationCompleteEvent;
import com.wolfalone.springsecuritybasic.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener
        implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private IUserService userService;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationToken(user, token);
        String url = event.getApplicationUrl() + "/verifyRegistration?token=" + token;

        log.info("Click the link to veriy : {}" + url);
    }
}
