package com.wolfalone.springsecuritybasic.controller;

import com.wolfalone.springsecuritybasic.entity.PasswordModel;
import com.wolfalone.springsecuritybasic.entity.User;
import com.wolfalone.springsecuritybasic.entity.VerificationToken;
import com.wolfalone.springsecuritybasic.event.RegistrationCompleteEvent;
import com.wolfalone.springsecuritybasic.model.UserModel;
import com.wolfalone.springsecuritybasic.service.IUserService;
import com.wolfalone.springsecuritybasic.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import static java.rmi.server.LogStream.log;

@RestController
@Slf4j
public class RegistrationRest {

    @Autowired
    private IUserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;


    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello bro");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserModel userModel
            , final HttpServletRequest request) {
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(request)
        ));
        return ResponseEntity.status(200).body("Success");
    }


    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User verifies succesfully";
        } else {
            return "Bad user!";
        }
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }

    @GetMapping("/resendverifytoken")
    public String resendVerificationToken(@RequestParam("token") String oldToken
            , HttpServletRequest request) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerificationTokenMail(user, applicationUrl(request), verificationToken);
        return "Verification Link Sent";
    }



    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest
                                request){
        User user = userService.findUserByEmail(passwordModel.getEmail());
        String url = "";
        if(user != null){
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            url = passwordResetTokenMail(user, applicationUrl(request), token);
        }
        return url;
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestParam("token") String token
            , @RequestBody PasswordModel passwordModel){
        String result = userService.validatePasswordResetToken(token);
        if(!result.equalsIgnoreCase("valid")){
            return result + " token";
        }
        Optional<User> user = userService.getUserByPasswordResetToken(token);
        if(user.isPresent()){
            userService.changePassword(user.get(), passwordModel.getNewPassword());
            return "Password reset successfully";
        } {
            return "Invalid token";
        }

    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel){
        User user = userService.findUserByEmail(passwordModel.getEmail());
        if(!userService.checkIfValidOldPassoword(user, passwordModel.getOldPassword())){
            return "Invalid old password";

        }
        userService.changePassword(user, passwordModel.getNewPassword());
        return "Password changed successfully";
    }
    private String passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url = applicationUrl
                + "/savePassword?token="
                + token;
        log("Click link to reset password: " + url);
        return url;

    }

    private void resendVerificationTokenMail(User user
            , String applicationUrl
            , VerificationToken verificationToken) {
        String url = applicationUrl
                + "/verifyRegistration?token="
                + verificationToken.getToken();
        log(url);
    }

}
