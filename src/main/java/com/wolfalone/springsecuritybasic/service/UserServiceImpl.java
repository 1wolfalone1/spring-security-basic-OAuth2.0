package com.wolfalone.springsecuritybasic.service;

import com.wolfalone.springsecuritybasic.entity.PasswordResetToken;
import com.wolfalone.springsecuritybasic.entity.User;
import com.wolfalone.springsecuritybasic.entity.VerificationToken;
import com.wolfalone.springsecuritybasic.model.UserModel;
import com.wolfalone.springsecuritybasic.repository.IUserRepo;
import com.wolfalone.springsecuritybasic.repository.IVerificationTokenRepo;
import com.wolfalone.springsecuritybasic.repository.PasswordResetTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService{

    @Autowired
    private IUserRepo userRepo;


    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;

    @Autowired
    private IVerificationTokenRepo verificationTokenRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(UserModel userModel) {
        System.out.println(userModel);
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setFirstName((userModel.getFirstName()));
        user.setLastName(userModel.getLastName());
        user.setRole("Role");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userRepo.save(user);
        return user;
    }

    @Override
    public void saveVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepo.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken =
                verificationTokenRepo.findByToken(token);
        if(verificationToken == null){
            return "invalid";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();

        if(verificationToken.getExpirationTime().getTime() <= cal.getTime().getTime()){
            verificationTokenRepo.delete(verificationToken);
            return "invalid";
        }
        user.setEnabled(true);
        userRepo.save(user);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepo.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepo.save(verificationToken);
        return verificationToken;

    }

    @Override
    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, token);
        passwordResetTokenRepo.save(passwordResetToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken =
                passwordResetTokenRepo.findByToken(token);
        if(passwordResetToken == null){
            return "invalid";
        }

        User user = passwordResetToken.getUser();
        Calendar cal = Calendar.getInstance();

        if(passwordResetToken.getExpirationTime().getTime() <= cal.getTime().getTime()){
            passwordResetTokenRepo.delete(passwordResetToken);
            return "expired";
        }
        user.setEnabled(true);
        userRepo.save(user);
        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepo.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    @Override
    public boolean checkIfValidOldPassoword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }
}
