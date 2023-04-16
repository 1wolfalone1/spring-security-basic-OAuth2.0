package com.wolfalone.springsecuritybasic.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordModel {


    private String oldPassword;
    private String newPassword;
    private String email;
}
