package com.musiciantrainer.musiciantrainerproject.controller;

import com.musiciantrainer.musiciantrainerproject.entity.HomePageViewModel;
import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.service.PieceService;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.List;

@Controller
public class MainController {

    private UserService userService;
    private PieceService pieceService;


    @Autowired
    public MainController(UserService userService, PieceService pieceService) {
        this.userService = userService;
        this.pieceService = pieceService;
    }

    @GetMapping("/")
    public String showHome(Model model, Authentication authentication) {

        String userEmail = authentication.getName(); // Get the email from principal

        User theUser = userService.findUserByEmail(userEmail);

        List<Piece> pieces = pieceService.getPiecesByUserOrderedByPriorityAndDaysPassed(theUser);

        HomePageViewModel theHomePageViewModel = new HomePageViewModel(pieces);

        model.addAttribute("homePageViewModel", theHomePageViewModel);

        return "home"; // This is a Thymeleaf template name
    }

    @GetMapping("/admin")
    public String showAdmin() {

        return "admin";
    }

}
