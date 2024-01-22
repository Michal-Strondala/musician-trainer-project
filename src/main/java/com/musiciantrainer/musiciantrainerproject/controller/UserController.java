package com.musiciantrainer.musiciantrainerproject.controller;

import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import com.musiciantrainer.musiciantrainerproject.user.WebUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.logging.Logger;

@Controller
@RequestMapping("/user")
public class UserController {

    private Logger logger = Logger.getLogger(getClass().getName());

    private UserService userService;

    // constructor injection
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showFormForEdit")
    public String showFormForEdit(
            Model theModel, Principal principal) {
        // Get the currently authenticated user's email (username in your case)
        String email = principal.getName();

        // Get the user from the service based on the email
        User theUser = userService.findUserByEmail(email);

        // Set user in the model to prepopulate the form
        theModel.addAttribute("user", theUser);

        // Add an empty WebUser object to the model for the form
        theModel.addAttribute("webUser", new WebUser());

        // Send over to our form
        return "edit-profile";
    }
    @PostMapping("/processFormForEdit")
    public String processFormForEdit(
            @Valid @ModelAttribute("webUser") WebUser theWebUser,
            BindingResult theBindingResult,
            HttpSession session, Model theModel, RedirectAttributes redirectAttributes, Principal principal) {

        String email = theWebUser.getEmail();

        logger.info("Processing edit form for: " + email);

        // form validation
        if (theBindingResult.hasErrors()){
            return "edit-profile";
        }

        // check if the edited email already exists in the database
        User existingUserWithEmail = userService.findUserByEmail(email);

        if (existingUserWithEmail != null && !existingUserWithEmail.getEmail().equals(principal.getName())) {
            logger.warning("Email already in use.");
            redirectAttributes.addFlashAttribute("error", "emailExists");
            return "redirect:/user/showFormForEdit";
        }

        // get the currently authenticated user's email (username/name in your case)
        String currentUserEmail = principal.getName();

        // get the user from the service based on the email
        User existingUser = userService.findUserByEmail(currentUserEmail);

        // update existing user with new information
        existingUser.setName(theWebUser.getName());
        existingUser.setEmail(theWebUser.getEmail());
        existingUser.setPassword(theWebUser.getPassword()); // handle password hashing appropriately

        // save the updated user
        userService.updateUser(existingUser);

        logger.info("Successfully edited user: " + email);

        // Update the principal object with the updated user details
        UserDetails updatedPrincipal = userService.loadUserByUsername(existingUser.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(updatedPrincipal,
                existingUser.getPassword(), updatedPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // place user in the web http session for later use
        session.setAttribute("user", theWebUser);

        // saving successful edit message into the model for redirecting
        redirectAttributes.addFlashAttribute("successMessage", "You have successfully edited your profile!");

        return "redirect:/home?success";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") WebUser theWebUser) {

        // save the employee
        userService.save(theWebUser);

        // use the redirect to prevent duplicate submissions
        return "redirect:/home";
    }

    // update user
    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User theUser) {
        // Update user details in the database
        userService.updateUser(theUser);
        return "redirect:/home"; // Redirect to the home page or any other appropriate page
    }
}
