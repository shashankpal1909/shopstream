package com.shashank.auth_service.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
public class UserInfoController {

    @GetMapping
    public String me(@AuthenticationPrincipal UserDetails user) {
        return "Logged in as: " + user.getUsername();
    }
}
