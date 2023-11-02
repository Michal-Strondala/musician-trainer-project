package com.musiciantrainer.musiciantrainerproject.controller;

import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.service.PieceService;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import com.musiciantrainer.musiciantrainerproject.user.WebUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;
@Controller
@RequestMapping("/pieces")
public class PieceController {
    private Logger logger = Logger.getLogger(getClass().getName());

    private UserService userService;

    private PieceService pieceService;

    // constructor injection
    @Autowired
    public PieceController(UserService userService, PieceService pieceService) {
        this.userService = userService;
        this.pieceService = pieceService;
    }

    @GetMapping("/showAddPieceForm")
    public String showAddPieceForm(
            Model theModel, Principal principal, HttpSession session) {
        // Get the currently authenticated user's email (username in your case)
        String email = principal.getName();

        // Get the user from the service based on the email
        User theUser = userService.findUserByEmail(email);

        // Set user in the model to prepopulate the form
        theModel.addAttribute("user", theUser);

        // Add an empty Piece object to the model for the form
        theModel.addAttribute("piece", new Piece());

        // Add an empty WebUser object to the model for the form
        theModel.addAttribute("webUser", new WebUser());

        // Send over to our form
        return "pieces/add-piece";
    }

    @PostMapping("/processAddPieceForm")
    public String processAddPieceForm(@ModelAttribute("piece") Piece thePiece, Authentication authentication,
                                      RedirectAttributes redirectAttributes) {

        User theUser = userService.findUserByEmail(authentication.getName());

        pieceService.addPiece(thePiece, theUser);

        // Add a success message to be displayed on the redirected page
        redirectAttributes.addFlashAttribute("success", true);

        return "redirect:/"; // When piece is added successfully, it will show a success message
    }

    @GetMapping("/showFormForEdit")
    public String showFormForEdit(@RequestParam("pieceId") Long theId, Model theModel) {

        // get the employee from the service
        Piece thePiece = pieceService.getPieceById(theId);

        // set employee in the model to prepopulate the form
        theModel.addAttribute("piece", thePiece);

        // send over to our form
        return "pieces/edit-piece";
    }

    @PostMapping("/editPiece")
    public String editPiece(@ModelAttribute("piece") Piece thePiece, Authentication authentication) {

        // Fetch the user from the database based on the current user's authentication details
        User theUser = userService.findUserByEmail(authentication.getName());

        // save the piece
        pieceService.editPiece(thePiece, theUser);

        // use the redirect to prevent duplicate submissions
        return "redirect:/";
    }

    @GetMapping("/deletePiece")
    public String deletePiece(@RequestParam("pieceId") Long theId) {

        // delete the piece
        pieceService.deletePiece(theId);

        // redirect to /
        return "redirect:/";
    }

}
