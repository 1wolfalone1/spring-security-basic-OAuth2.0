package com.wolfalone.springsecuritybasic.service;

import com.wolfalone.springsecuritybasic.entity.User;
import com.wolfalone.springsecuritybasic.entity.VerificationToken;
import com.wolfalone.springsecuritybasic.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface IUserService {

    User registerUser(UserModel userModel);

    void saveVerificationToken(User user, String token);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    User findUserByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassoword(User user, String oldPassword);
}
