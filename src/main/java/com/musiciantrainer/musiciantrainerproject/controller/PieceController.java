package com.musiciantrainer.musiciantrainerproject.controller;

import com.musiciantrainer.musiciantrainerproject.dto.PieceLogViewModel;
import com.musiciantrainer.musiciantrainerproject.entity.*;
import com.musiciantrainer.musiciantrainerproject.service.PieceService;
import com.musiciantrainer.musiciantrainerproject.service.PlanPieceService;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import com.musiciantrainer.musiciantrainerproject.dto.WebUser;
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
    private PlanPieceService planPieceService;

    // constructor injection
    @Autowired
    public PieceController(UserService userService, PieceService pieceService, PlanPieceService planPieceService) {
        this.userService = userService;
        this.pieceService = pieceService;
        this.planPieceService = planPieceService;
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

        return "redirect:/home"; // When piece is added successfully, it will show a success message
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
            // Fetch the existing piece from the database
            Piece existingPiece = pieceService.getPieceById(thePiece.getId());

            // Check if the existing piece exists and belongs to the authenticated user
            if (existingPiece == null || !existingPiece.getUser().equals(theUser)) {
                // Handle the error appropriately
                return "redirect:/error";
            }

            // Update the existing piece with the modified values
            existingPiece.setName(thePiece.getName());
            existingPiece.setComposer(thePiece.getComposer());
            existingPiece.setPriority(thePiece.getPriority());

            // Edit the piece
            pieceService.editPiece(existingPiece, theUser);

            // Add a success message to be displayed on the redirected page
            redirectAttributes.addFlashAttribute("successEdit", true);

        } catch (DataIntegrityViolationException exception) {
            // Handle unique constraint violation (piece name already exists)
            redirectAttributes.addFlashAttribute("error", "Piece with the same name already exists.");
            redirectAttributes.addFlashAttribute("pieceId", thePiece.getId());
            return "redirect:/pieces/showFormForEdit?pieceId=" + thePiece.getId();
        }

        // Use the redirect to prevent duplicate submissions
        return "redirect:/home";
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
        return "redirect:/home";
    }

    // Record part
    @GetMapping("/showAddRecordForm")
    public String showAddRecordForm(@RequestParam(name = "pieceId", required = false) Long pieceId,
                                    @RequestParam("source") String source,
                                    @RequestParam(name = "trainingTime", required = false) String trainingTime,
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

        theModel.addAttribute("source", source);
        // Add trainingTime to the model if it's not null
        if (trainingTime != null) {
            theModel.addAttribute("trainingTime", trainingTime);
        }

        // Send over to our form
        return "pieces/add-record";
    }

    @PostMapping("/processAddRecordForm")
    public String processAddRecordForm(@RequestParam("pieceId") Long pieceId,
                                       @RequestParam("date") String dateString,
                                       @RequestParam("note") String note,
                                       @RequestParam("source") String source,
                                       @RequestParam(name = "trainingTime", required = false) String trainingTime,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {

        // Fetch the user from the database based on the current user's authentication details
        User theUser = userService.findUserByEmail(authentication.getName());

        // Get the selected piece based on pieceId
        Piece selectedPiece = pieceService.getPieceById(pieceId);

        // Parse the date-time string into LocalDateTime (you might need to adjust the date-time format)
        LocalDate parsedDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        // Create a new PieceLog object with the selected piece, parsed date-time, and note
        PieceLog pieceLog = new PieceLog(parsedDate, note);
        pieceLog.setPiece(selectedPiece);

        // Add the PieceLog record to the selected piece
        selectedPiece.add(pieceLog);

        // Save the updated piece (this will also save the PieceLog record due to CascadeType)
        pieceService.editPiece(selectedPiece, theUser);

        // Add a success message to be displayed on the redirected page
        redirectAttributes.addFlashAttribute("success", true);

        // Redirect based on the source
        if ("home".equals(source)) {
            return "redirect:/home?recordSuccess";
        } else if ("myplan".equals(source) && trainingTime != null) {
            return "redirect:/myPlan?trainingTime=" + trainingTime;
        } else {
            return "redirect:/home?recordSuccess";
        }

        //return "redirect:/home?recordSuccess"; // When the record is added successfully, it will show a success message
    }

    @GetMapping("/getRecordsByPiece")
    public String getRecordsByPiece(@RequestParam(name = "pieceId", required = false) Long pieceId,
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

        // Get the piece from the service based on the id
        Piece thePiece = pieceService.getPieceById(pieceId);

        // Add piece to the model for the form
        theModel.addAttribute("piece", thePiece);

        // Get the pieces from the service based on the user
        List<PieceLog> thePieceLogs = pieceService.getPieceLogsByPieceIdOrderedByDate(pieceId);

        // Add pieceLogs to the model for the form
        theModel.addAttribute("pieceLogs", thePieceLogs);

        // Add an empty WebUser object to the model for the form
        theModel.addAttribute("webUser", new WebUser());

        if (pieceId != null) {
            // Pre-select a specific piece
            theModel.addAttribute("pieceId", pieceId);
        }

        // Send over to our form
        return "pieces/get-records-by-piece";
    }

    @GetMapping("/getAllRecords")
    public String getAllRecords(Model theModel, Principal principal) {
        // Get the currently authenticated user's email (username in your case)
        String email = principal.getName();

        // Get the user from the service based on the email
        User theUser = userService.findUserByEmail(email);

        // Set user in the model to prepopulate the form
        theModel.addAttribute("user", theUser);


        List<PieceLog> pieceLogs = pieceService.getPieceLogsByUserOrderedByDate(theUser);
        PieceLogViewModel thePieceLogViewModel = new PieceLogViewModel(pieceLogs);

        theModel.addAttribute("pieceLogViewModel", thePieceLogViewModel);

        return "pieces/get-all-records"; // This is a Thymeleaf template name
    }

    @GetMapping("/showEditRecordForm")
    public String showEditRecordForm(@RequestParam("pieceLogId") Long pieceLogId,
                                     @RequestParam("source") String source,
                                     @RequestParam(value = "pieceId", required = false) Long pieceId,
                                     Model theModel, Principal principal) {
        String email = principal.getName();
        User theUser = userService.findUserByEmail(email);
        theModel.addAttribute("user", theUser);

        PieceLog thePieceLog = pieceService.getPieceLogById(pieceLogId);

        // Add pieces to the model for the dropdown
        List<Piece> pieces = pieceService.getPiecesByUser(theUser);
        theModel.addAttribute("pieces", pieces);

        // Set the selectedPieceId for the dropdown
        if (thePieceLog != null && thePieceLog.getPiece() != null) {
            theModel.addAttribute("selectedPieceId", thePieceLog.getPiece().getId());
        }

        theModel.addAttribute("pieceLog", thePieceLog);
        theModel.addAttribute("source", source);
        if (pieceId != null) {
            theModel.addAttribute("pieceId", pieceId);
        }

        return "pieces/edit-record";
    }

    @PostMapping("/processEditRecordForm")
    public String processEditRecordForm(@RequestParam("pieceLogId") Long pieceLogId, // get pieceLogId separately
                                        @RequestParam("pieceId") Long pieceId,
                                        @RequestParam("date") String dateString,
                                        @RequestParam("note") String note, // get note separately
                                        @RequestParam("source") String source,
                                        RedirectAttributes redirectAttributes) {
        try {
            // Parse the date string into LocalDate
            LocalDate parsedDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            // Fetch the PieceLog from the database using pieceLogId
            PieceLog existingPieceLog = pieceService.getPieceLogById(pieceLogId);
            if (existingPieceLog != null) {
                Piece piece = pieceService.getPieceById(pieceId);
                existingPieceLog.setPiece(piece);
                existingPieceLog.setDate(parsedDate); // Set the parsed date
                existingPieceLog.setNote(note); // Set the note

                // Save the updated PieceLog
                pieceService.editPieceLog(existingPieceLog);
                redirectAttributes.addFlashAttribute("successEditRecord", true);
            } else {
                redirectAttributes.addFlashAttribute("error", "Record not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to edit the record. Error: " + e.getMessage());
        }

        // Redirect based on the source
        if ("all".equals(source)) {
            return "redirect:/pieces/getAllRecords";
        } else if ("single".equals(source) && pieceId != null) {
            return "redirect:/pieces/getRecordsByPiece?pieceId=" + pieceId;
        } else {
            return "redirect:/home?recordSuccess";
        }
    }

    @GetMapping("/deleteRecord")
    public String deleteRecord(@RequestParam("pieceLogId") Long pieceLogId,
                               RedirectAttributes redirectAttributes) {
        try {
            // Delete the PieceLog based on the pieceLogId
            pieceService.deletePieceLog(pieceLogId);

            // Add a success message to be displayed on the redirected page
            redirectAttributes.addFlashAttribute("successDeleteRecord", true);
        } catch (Exception e) {
            // Handle exceptions, e.g., if the record does not exist

            // Add an error message to be displayed on the redirected page
            redirectAttributes.addFlashAttribute("error", "Failed to delete the record.");
        }

        return "redirect:/home?recordSuccess"; // Redirect to the appropriate page
    }

}
