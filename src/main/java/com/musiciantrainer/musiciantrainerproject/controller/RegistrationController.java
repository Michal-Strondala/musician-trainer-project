package com.musiciantrainer.musiciantrainerproject.controller;

import com.musiciantrainer.musiciantrainerproject.service.UserService;
import com.musiciantrainer.musiciantrainerproject.user.WebUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private Logger logger = Logger.getLogger(getClass().getName());

    private UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showRegistrationForm")
    public String showLoginPage(Model theModel) {

        theModel.addAttribute("webUser", new WebUser());

        return "register/registration-form";
    }

    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("webUser") WebUser theWebUser,
            BindingResult theBindingResult,
            HttpSession session, RedirectAttributes redirectAttributes) {

            String email = theWebUser.getEmail();
        try {
            logger.info("Processing registration form for: " + email);

            // form validation
            if (theBindingResult.hasErrors()){
                return "register/registration-form";
            }

            userService.save(theWebUser);;
        } catch (DataIntegrityViolationException exception) {
            logger.warning("Email already in use.");
            redirectAttributes.addFlashAttribute("error", "emailExists"); // Add error message as flash attribute
            return "redirect:/register/showRegistrationForm"; // Redirect to registration form
        }

        logger.info("Successfully created user: " + email);

        // place user in the web http session for later use
        session.setAttribute("user", theWebUser);

        // saving successful registration message into the model for redirecting
        redirectAttributes.addFlashAttribute("successMessage", "You have successfully registered to our awesome app!");

        return "redirect:/showLoginPage"; // When user registers successfully, it will show a success message
    }
}
