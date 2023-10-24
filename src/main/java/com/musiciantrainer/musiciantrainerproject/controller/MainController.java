package com.musiciantrainer.musiciantrainerproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {


    @GetMapping("/")
    public String showHome() {

        return "home";
    }

    @GetMapping("/admin")
    public String showAdmin() {

        return "admin";
    }

}
