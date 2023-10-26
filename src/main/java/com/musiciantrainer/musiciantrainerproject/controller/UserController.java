package com.musiciantrainer.musiciantrainerproject.controller;

import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import com.musiciantrainer.musiciantrainerproject.user.WebUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    // constructor injection
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/showFormForEdit")
    public String showFormForEdit(Model theModel, Principal principal) {
        // Get the currently authenticated user's email (username in your case)
        String email = principal.getName();

        // Get the user from the service based on the email
        User theUser = userService.findUserByEmail(email);

        // Set user in the model to prepopulate the form
        theModel.addAttribute("user", theUser);

        // Send over to our form
        return "edit-profile";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") WebUser theWebUser) {

        // save the employee
        userService.save(theWebUser);

        // use the redirect to prevent duplicate submissions
        return "redirect:/";
    }
}
