package com.example.toygry.one_you.Users.dto;

import lombok.Data;

@Data
public class UserInsertRequest {
    private String username;
    private String password;
    private String role;
    private String name;
    private String email;
    private String sPhoneNumber;
    private String pPhoneNumber;
}
