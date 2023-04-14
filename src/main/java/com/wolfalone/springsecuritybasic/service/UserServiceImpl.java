package com.wolfalone.springsecuritybasic.service;

import com.wolfalone.springsecuritybasic.entity.User;
import com.wolfalone.springsecuritybasic.entity.VerificationToken;
import com.wolfalone.springsecuritybasic.model.UserModel;
import com.wolfalone.springsecuritybasic.repository.IUserRepo;
import com.wolfalone.springsecuritybasic.repository.IVerificationTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService{

    @Autowired
    private IUserRepo userRepo;

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
}
