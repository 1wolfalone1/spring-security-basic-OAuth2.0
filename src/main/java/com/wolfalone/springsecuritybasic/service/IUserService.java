package com.wolfalone.springsecuritybasic.service;

import com.wolfalone.springsecuritybasic.entity.User;
import com.wolfalone.springsecuritybasic.entity.VerificationToken;
import com.wolfalone.springsecuritybasic.model.UserModel;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {

    User registerUser(UserModel userModel);

    void saveVerificationToken(User user, String token);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);
}
