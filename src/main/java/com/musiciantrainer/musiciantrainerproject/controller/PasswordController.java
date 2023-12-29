package com.musiciantrainer.musiciantrainerproject.controller;

import com.musiciantrainer.musiciantrainerproject.entity.PasswordResetToken;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.security.SecurityUserService;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import com.musiciantrainer.musiciantrainerproject.service.email.EmailService;
import com.musiciantrainer.musiciantrainerproject.user.PasswordDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Controller
@RequestMapping("/password")
public class PasswordController {
    private Logger logger = Logger.getLogger(getClass().getName());

    private UserService userService;
    private EmailService emailService;
    private SecurityUserService securityUserService;
    private JavaMailSender javaMailSender;
    private MessageSource messages;


    @Autowired
    public PasswordController(UserService userService, EmailService emailService,
                              SecurityUserService securityUserService, JavaMailSender javaMailSender,
                              MessageSource messages) {
        this.userService = userService;
        this.emailService = emailService;
        this.securityUserService = securityUserService;
        this.javaMailSender = javaMailSender;
        this.messages = messages;
    }


    // Reset password
    @PostMapping("/resetPassword")
    public String resetPassword(HttpServletRequest request, @RequestParam("email") final String userEmail, RedirectAttributes attributes) {
        final User theUser = userService.findUserByEmail(userEmail);

        if (theUser != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(theUser, token);

            // Log messages for debugging
            System.out.println("Reset Password: Token created for user " + userEmail);

            try {
                javaMailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, theUser));
                System.out.println("Reset Password: Email sent successfully");

                // Add success message to redirect attributes
                attributes.addFlashAttribute("successMessage", messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));

                // Redirect to login page
                return "redirect:/showLoginPage";
            } catch (Exception e) {
                System.out.println("Reset Password: Error sending email - " + e.getMessage());

                // Add error message to redirect attributes
                attributes.addFlashAttribute("errorMessage", "Error sending reset password email.");

                // Redirect to the reset password page
                return "redirect:/password/resetPassword";
            }
        }

        // Add user not found message to redirect attributes
        attributes.addFlashAttribute("errorMessage", "User not found.");

        // Redirect to the reset password page
        return "redirect:/password/resetPassword";
    }


    // Change password
    @GetMapping("/changePassword/{token}")
    public String showChangePasswordPage(Model model, @PathVariable String token) {
        String result = securityUserService.validatePasswordResetToken(token);

        if (result != null) {
            String messageKey = "auth.message." + result;
            model.addAttribute("messageKey", messageKey);
            return "forward:/showLoginPage";  // Use forward instead of redirect
        } else {
            PasswordDto passwordDto = new PasswordDto(token);
            model.addAttribute("passwordDto", passwordDto);

            // Forward to updatePassword with the token in the path
            return "forward:/password/updatePassword/" + token;  // Use forward instead of redirect
        }
    }
//    @GetMapping("/changePassword")
//    public String showChangePasswordPage(Model model, @RequestParam("token") String token) {
//        String result = securityUserService.validatePasswordResetToken(token);
//
//        if (result != null) {
//            String messageKey = "auth.message." + result;
//            model.addAttribute("messageKey", messageKey);
//            return "redirect:/showLoginPage";
//        } else {
//            model.addAttribute("token", token);
//            return "redirect:/password/updatePassword";
//        }
//    }

    @GetMapping("/updatePassword/{token}")
    public String updatePassword(HttpServletRequest request, Model model, @PathVariable String token) {
        Locale locale = request.getLocale();
        model.addAttribute("lang", locale.getLanguage());

        String messageKey = (String) model.getAttribute("messageKey");
        if (messageKey != null) {
            String message = messages.getMessage(messageKey, null, locale);
            model.addAttribute("message", message);
        }

        // Add the PasswordDto to the model with the retrieved token
        model.addAttribute("passwordDto", new PasswordDto(token));

        return "password/updatePassword";
    }

//    @GetMapping("/updatePassword")
//    public String updatePassword(HttpServletRequest request, Model model) {
//        Locale locale = request.getLocale();
//        model.addAttribute("lang", locale.getLanguage());
//
//        String messageKey = (String) model.getAttribute("messageKey");
//        if (messageKey != null) {
//            String message = messages.getMessage(messageKey, null, locale);
//            model.addAttribute("message", message);
//        }
//
//        // Add the PasswordDto to the model
//        model.addAttribute("passwordDto", new PasswordDto());
//
//        return "password/updatePassword";
//    }

//    @GetMapping("/updatePassword")
//    public String updatePassword(HttpServletRequest request, Model model,
//                                 @RequestParam("messageKey") Optional<String> messageKey) {
//        Locale locale = request.getLocale();
//        model.addAttribute("lang", locale.getLanguage());
//
//        messageKey.ifPresent(key -> {
//            String message = messages.getMessage(key, null, locale);
//            model.addAttribute("message", message);
//        });
//
//        // Add the PasswordDto to the model
//        model.addAttribute("passwordDto", new PasswordDto());
//
//        return "password/updatePassword";
//    }


    @PostMapping("/savePassword")
    public String savePassword(
            final Locale locale,
            @Valid PasswordDto passwordDto,
            BindingResult bindingResult,
            HttpSession session,
            Model model, RedirectAttributes redirectAttributes) {

        String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

        if (result != null) {
            // Add error message to model
            model.addAttribute("errorMessage", messages.getMessage("auth.message." + result, null, locale));
            return "password/updatePassword"; // Return the same page
        }

        Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if (user.isPresent()) {
            // Check if the entered email matches the one associated with the user
            String enteredEmail = passwordDto.getEmail();
            String userAssociatedEmail = user.get().getEmail();
            System.out.println("Entered Email: " + enteredEmail);
            System.out.println("User's Associated Email: " + userAssociatedEmail);

            if (!user.get().getEmail().equals(passwordDto.getEmail())) {
                // Manually add a field error for Thymeleaf to display
                bindingResult.addError(new FieldError("passwordDto", "email", "Please provide a valid email address."));
            }

            if (bindingResult.hasErrors()) {
                // If there are validation errors, return the same page
                return "password/updatePassword";
            }

            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());

            // Delete the password reset token since the password change was successful
            deletePasswordResetToken(passwordDto.getToken());

            // Add success message as a flash attribute
            redirectAttributes.addFlashAttribute("successMessage", messages.getMessage("message.resetPasswordSuc", null, locale));

            // Redirect to the login page upon successful password change
            return "redirect:/showLoginPage";
        } else {
            // Add error message to model
            model.addAttribute("errorMessage", messages.getMessage("auth.message.invalid", null, locale));
            return "password/updatePassword"; // Return the same page
        }
    }
//    @PostMapping("/savePassword")
//    public String savePassword(final Locale locale, @Valid PasswordDto passwordDto, HttpSession session) {
//        String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());
//
//        if (result != null) {
//            // Add error message to session
//            session.setAttribute("errorMessage", messages.getMessage("auth.message." + result, null, locale));
//            return "redirect:/password/updatePassword";
//        }
//
//        Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
//        if (user.isPresent()) {
//            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
//
//            // Delete the password reset token since the password change was successful
//            deletePasswordResetToken(passwordDto.getToken());
//
//            // Add success message to session
//            session.setAttribute("successMessage", messages.getMessage("message.resetPasswordSuc", null, locale));
//
//            // Redirect to the login page upon successful password change
//            return "redirect:/showLoginPage";
//        } else {
//            // Add error message to session
//            session.setAttribute("errorMessage", messages.getMessage("auth.message.invalid", null, locale));
//            return "redirect:/password/updatePassword";
//        }
//    }
//    @PostMapping("/savePassword")
//    public String savePassword(final Locale locale, @Valid PasswordDto passwordDto, Model model) {
//        System.out.println("1. Getting the token: " + passwordDto.getToken());
//        String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());
//
//        if (result != null) {
//            model.addAttribute("message", messages.getMessage("auth.message." + result, null, locale));
//            return "password/updatePassword";
//        }
//
//        Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
//        if (user.isPresent()) {
//            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
//            model.addAttribute("message", messages.getMessage("message.resetPasswordSuc", null, locale));
//        } else {
//            model.addAttribute("message", messages.getMessage("auth.message.invalid", null, locale));
//        }
//        return "password/updatePassword";
//    }


    // ============== NON-API ============

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final User theUser) {
        final String url = contextPath + "/password/changePassword/" + token;
        final String message = messages.getMessage("message.resetPasswordEmailContent", null, locale);
        return emailService.constructEmail("Reset Password", message + " \r\n" + url, theUser);
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
    // Helper method to delete the password reset token
    private void deletePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = userService.getPasswordResetToken(token);
        if (passwordResetToken != null) {
            userService.deletePasswordResetToken(passwordResetToken);
        }
    }
}
