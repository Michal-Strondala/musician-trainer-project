package com.musiciantrainer.musiciantrainerproject.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/showLoginPage")
    public String showLoginPage(Model model, HttpSession session) {
        // Check for success message in session
        if (session.getAttribute("successMessage") != null) {
            model.addAttribute("successMessage", session.getAttribute("successMessage"));
            // Clear the session attribute to show the message only once
            session.removeAttribute("successMessage");
        }

        return "fancy-login";
    }

    // add request mapping for /access-denied

    @GetMapping("/access-denied")
    public String showAccessDenied() {

        return "access-denied";
    }

    @GetMapping("/password/forgetPasswordPage")
    public String showForgetPasswordPage() {
        System.out.println("Showing forgetPasswordPage");

        return "password/forgetPassword";
    }
}
