package com.musiciantrainer.musiciantrainerproject.controller;

import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.service.PieceService;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import com.musiciantrainer.musiciantrainerproject.user.WebUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            Model theModel, Principal principal) {
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

        try {
            pieceService.addPiece(thePiece, theUser);

            // Add a success message to be displayed on the redirected page
            redirectAttributes.addFlashAttribute("success", true);

        } catch (DataIntegrityViolationException exception) {
            // Handle unique constraint violation (piece name already exists)
            redirectAttributes.addFlashAttribute("error", "Piece with the same name already exists.");
            return "redirect:/pieces/showAddPieceForm"; // Redirect to showAddPieceForm form
        }

        return "redirect:/"; // When piece is added successfully, it will show a success message
    }

    @GetMapping("/showFormForEdit")
    public String showFormForEdit(@RequestParam("pieceId") Long theId, Model theModel, Authentication authentication) {

        // Get the currently authenticated user's email (username in your case)
        String userEmail = authentication.getName();

        // Get the user from the service based on the email
        User theUser = userService.findUserByEmail(userEmail);

        // Set user in the model
        theModel.addAttribute("user", theUser);

        // get the piece from the service
        Piece thePiece = pieceService.getPieceById(theId);

        // Check if the piece exists and if the authenticated user is the owner of the piece
        if (thePiece == null || !thePiece.getUser().getEmail().equals(userEmail)) {
            // Piece doesn't exist or the authenticated user is not the owner, handle the error (redirect to an error page or show an error message)
            return "redirect:/error"; // Redirect to an error page or any other appropriate action
        }

        // set the piece in the model to prepopulate the form
        theModel.addAttribute("piece", thePiece);

        // send over to our form
        return "pieces/edit-piece";
    }

    @PostMapping("/editPiece")
    public String editPiece(@ModelAttribute("piece") Piece thePiece, Authentication authentication,
                            RedirectAttributes redirectAttributes) {

        // Fetch the user from the database based on the current user's authentication details
        User theUser = userService.findUserByEmail(authentication.getName());

        try {
            // save the piece
            pieceService.editPiece(thePiece, theUser);

            // Add a success message to be displayed on the redirected page
            redirectAttributes.addFlashAttribute("successEdit", true);

        } catch (DataIntegrityViolationException exception) {

            // Handle unique constraint violation (piece name already exists)
            redirectAttributes.addFlashAttribute("error", "Piece with the same name already exists.");

            // Add the piece ID to the flash attributes to identify which piece was being edited
            redirectAttributes.addFlashAttribute("pieceId", thePiece.getId());

            return "redirect:/pieces/showFormForEdit?pieceId=" + thePiece.getId(); // Redirect to showFormForEdit form
        }


        // use the redirect to prevent duplicate submissions
        return "redirect:/";
    }


    @GetMapping("/deletePiece")
    public String deletePiece(@RequestParam("pieceId") Long theId, Authentication authentication) {
        // Fetch the user from the database based on the current user's authentication details
        User authenticatedUser = userService.findUserByEmail(authentication.getName());

        // Fetch the original piece from the database
        Piece originalPiece = pieceService.getPieceById(theId);

        // Check if the authenticated user is the owner of the piece
        if (originalPiece.getUser().getId().equals(authenticatedUser.getId())) {
            // delete the piece
            pieceService.deletePiece(theId);
        }

        // redirect to home page
        return "redirect:/";
    }

    // Record part
    @GetMapping("/showAddRecordForm")
    public String showAddRecordForm(@RequestParam(name = "pieceId", required = false) Long pieceId,
                                    Model theModel, Principal principal) {

        // Get the currently authenticated user's email (username in your case)
        String email = principal.getName();

        // Get the user from the service based on the email
        User theUser = userService.findUserByEmail(email);

        // Set user in the model to prepopulate the form
        theModel.addAttribute("user", theUser);

        // Get the pieces from the service based on the user
        List<Piece> thePieces = pieceService.getPiecesByUserOrderedByPriorityAndDaysPassed(theUser);

        // Add pieces to the model for the form
        theModel.addAttribute("pieces", thePieces);

        // Add an empty WebUser object to the model for the form
        theModel.addAttribute("webUser", new WebUser());

        if (pieceId != null) {
            // Pre-select a specific piece
            theModel.addAttribute("selectedPieceId", pieceId);
        }

        // Send over to our form
        return "pieces/add-record";
    }

    @PostMapping("/processAddRecordForm")
    public String processAddRecordForm(@RequestParam("pieceId") Long pieceId,
                                       @RequestParam("date") String dateString,
                                       @RequestParam("note") String note,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {

        // Fetch the user from the database based on the current user's authentication details
        User theUser = userService.findUserByEmail(authentication.getName());

        // Get the selected piece based on pieceId
        Piece selectedPiece = pieceService.getPieceById(pieceId);

        // Parse the date-time string into LocalDateTime (you might need to adjust the date-time format)
        LocalDate parsedDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        // Create a new PieceLog object with the selected piece, parsed date-time, and note
        PieceLog pieceLog = new PieceLog(parsedDate, note);
        pieceLog.setPiece(selectedPiece);

        // Add the PieceLog record to the selected piece
        selectedPiece.add(pieceLog);

        // Save the updated piece (this will also save the PieceLog record due to CascadeType)
        pieceService.editPiece(selectedPiece, theUser);

        // Add a success message to be displayed on the redirected page
        redirectAttributes.addFlashAttribute("success", true);

        return "redirect:/?recordSuccess"; // When the record is added successfully, it will show a success message
    }

}
