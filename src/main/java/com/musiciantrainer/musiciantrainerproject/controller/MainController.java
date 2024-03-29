package com.musiciantrainer.musiciantrainerproject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musiciantrainer.musiciantrainerproject.dto.CreatedPlansViewModel;
import com.musiciantrainer.musiciantrainerproject.dto.HomePageViewModel;
import com.musiciantrainer.musiciantrainerproject.dto.MyPlanViewModel;
import com.musiciantrainer.musiciantrainerproject.entity.*;
import com.musiciantrainer.musiciantrainerproject.service.PieceService;
import com.musiciantrainer.musiciantrainerproject.service.PlanPieceService;
import com.musiciantrainer.musiciantrainerproject.service.PlanService;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import com.musiciantrainer.musiciantrainerproject.service.email.EmailService;
import com.musiciantrainer.musiciantrainerproject.utilities.TrainingTimeUtil;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Controller
public class MainController {

    @Value("${openai.model}")
    private String aiModel;
    @Value("${openai.api.key}")
    private String apiKey;



    private UserService userService;
    private PieceService pieceService;
    private PlanService planService;
    private ObjectMapper objectMapper;
    private PlanPieceService planPieceService;
    private EmailService emailService;


    @Autowired
    public MainController(UserService userService, PieceService pieceService, PlanService planService, ObjectMapper objectMapper, PlanPieceService planPieceService, EmailService emailService) {
        this.userService = userService;
        this.pieceService = pieceService;
        this.planService = planService;
        this.objectMapper = objectMapper;
        this.planPieceService = planPieceService;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String showIndex(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName(); // Get the email from principal
            User theUser = userService.findUserByEmail(userEmail);

            if (theUser != null) {
                List<Piece> pieces = pieceService.getPiecesByUserOrderedByPriorityAndDaysPassed(theUser);
                HomePageViewModel theHomePageViewModel = new HomePageViewModel(pieces);

                model.addAttribute("homePageViewModel", theHomePageViewModel);
                model.addAttribute("user", theUser); // Přidejte uživatele do modelu

                return "redirect:/home"; // This is a Thymeleaf template name
            }
        }

        // User is not logged in or something went wrong
        return "index";
    }

    @GetMapping("/home")
    public String showHome(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName(); // Get the email from principal
            User theUser = userService.findUserByEmail(userEmail);

            if (theUser != null) {
                List<Piece> pieces = pieceService.getPiecesByUserOrderedByPriorityAndDaysPassed(theUser);
                HomePageViewModel theHomePageViewModel = new HomePageViewModel(pieces);

                model.addAttribute("homePageViewModel", theHomePageViewModel);
                model.addAttribute("user", theUser); // Přidejte uživatele do modelu

                return "home"; // This is a Thymeleaf template name
            }
        }

        // User is not logged in or something went wrong
        return "redirect:/";
    }

    @GetMapping("/myPlan")
    public String showMyPlan(@RequestParam("trainingTime") String trainingTime, Model model, Authentication authentication) {

        String userEmail = authentication.getName();
        User theUser = userService.findUserByEmail(userEmail);

        // převedení času z hodin na minuty
        String convertedTime = getHoursAsMinutes(trainingTime);

        Plan savedPlan;
        OpenAiService service = null;

        // kontrola, jestli daný plán již nebyl dnes vygenerovaný na daný počet minut
        if (doesPlanExist(convertedTime)) {
            savedPlan = planService.getPlanByTotalMinutesAndDate(Integer.parseInt(convertedTime), LocalDate.now());
        } else {

            // zalozeni objektu openai
            service = new OpenAiService(apiKey, Duration.ofSeconds(90));

            // system prompt
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "I want you to act as my music learning planner. \n" +
                    "You will help user to organize his daily routine plan of training music. \n" +
                    "You will decide what is the most important to train today. \n" +
                    "I will give you a list of pieces in JSON format. You will do user's schedule for the particular time.\n" +
                    "The user will give you his time for that day in minutes and/or hours.\n" +
                    "You will have priority in the list. \n" +
                    "The bigger the value of priority, the bigger the priority, and a low value is a low priority. \n" +
                    "A 0 priority is a piece without priority.\n" +
                    "Important is also \"formattedLastTrainingDate\", it is the date when the user practiced the exercise the last time. \n" +
                    "You can find the attribute \"numberOfDaysPassed\" which displays the number of days since the last training date. If the training time that user sets is 2 hours and more, include also some of the pieces that have not been trained for a long time even if they have no priority.\n" +
                    "You can find there \"numberOfTimesTrained\" which shows the number of times the user trained that piece.\n" +
                    "The goal is to not lose the memory of pieces. Each train session has to have 30 minutes (1 pomodoro) as a default time unless the particular piece's time is set differently, in the JSON should be \"time\", for example 20 or 15 minutes. In that case you do not have to set training time for particular piece for 30 minutes. \n" +
                    "\n" +
                    "Write in proper English.\n" +
                    "\n" +
                    "The output must be in JSON with time schedule included.\n" +
                    "\n" +
                    "This is example of requested JSON output:\n" +
                    "{" +
                    " \"planItems\" : [{" +
                    "\"time\": 30," +
                    "\"id\": 1" +
                    "}," +
                    " {" +
                    "\"time\": 20," +
                    "\"id\": 2" +
                    "}] " +
                    "}" +
                    "Do not include \"details\" and nothing else (like ```json etc.) \n" +
                    "Do not write any extra text in the end.");
            messages.add(systemMessage);

            //user prompt
            System.out.print("First Query: ");

            String userPrompt = pieceService.getPiecesDtoAsJsonString(theUser) + " My time for today is " + convertedTime + " minutes.";
            ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);
            messages.add(userMessage);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model(aiModel)
                    .messages(messages)
                    .n(1)
                    .maxTokens(256)
                    .build();

            ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();

            service.streamChatCompletion(chatCompletionRequest)
                    .doOnError(Throwable::printStackTrace)
                    .blockingForEach(System.out::println);

            // *** Process AI response ***
            // Deserealize AI reponse in the form of JSON into Java objects

            List<PlanItem> planItems = this.decodeJSON(responseMessage.getContent());

            // 1. Create Plan instance
            Plan newPlan = createPlanInstance(convertedTime, theUser);

            // 2. Assign PlanItems to PlanPieces and Plan
            List<PlanPiece> planPieces = assignPlanItemsToPiecesAndPlan(planItems, newPlan);

            // Set the list of PlanPieces to the Plan
            newPlan.setPlanPieces(planPieces);

            // 3. Save the Plan to the database
            savedPlan = planService.addPlan(newPlan);

            // 4. Save the PlanPieces to the database
            for (PlanPiece planPiece : planPieces) {
                planPieceService.addPlanPiece(planPiece);
            }
        }

        MyPlanViewModel myPlanViewModel = new MyPlanViewModel(savedPlan);

        // Add the plan to the model
        model.addAttribute("myPlanViewModel", myPlanViewModel);
        model.addAttribute("user", theUser);

        if (service != null) {
            service.shutdownExecutor();
        }

        return "myplan";
    }

    private boolean doesPlanExist(String convertedTime) {

        return planService.getPlanByTotalMinutesAndDate(Integer.parseInt(convertedTime), LocalDate.now()) != null;
    }

    @GetMapping("/createdPlans")
    public String showCreatedPlans(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName(); // Get the email from principal
            User theUser = userService.findUserByEmail(userEmail);

            if (theUser != null) {
                List<Plan> createdPlans = planService.getPlansByUserOrderedByTotalMinutes(theUser);
                CreatedPlansViewModel theCreatedPlansViewModel = new CreatedPlansViewModel(createdPlans);

                model.addAttribute("createdPlansViewModel", theCreatedPlansViewModel);
                model.addAttribute("user", theUser); // Přidejte uživatele do modelu

                return "created-plans"; // This is a Thymeleaf template name
            }
        }

        // Uživatel není přihlášen nebo se něco pokazilo
        return "redirect:/";

    }

    @GetMapping("/deletePlan")
    public String deletePlan(@RequestParam("planId") Long planId,
                               RedirectAttributes redirectAttributes) {
        try {
            // Delete the Plan based on the planId
            planService.deletePlan(planId);

            // Add a success message to be displayed on the redirected page
            redirectAttributes.addFlashAttribute("successDeletePlan", true);
        } catch (Exception e) {
            // Handle exceptions, e.g., if the plan does not exist

            // Add an error message to be displayed on the redirected page
            redirectAttributes.addFlashAttribute("error", "Failed to delete the plan.");
        }

        return "redirect:/createdPlans?recordSuccess"; // Redirect to the appropriate page
    }




    @GetMapping("/piecesToJson")
    public String piecesToJson(Model model, Authentication authentication) {
        String userEmail = authentication.getName();
        User theUser = userService.findUserByEmail(userEmail);

        String piecesJson = pieceService.getPiecesDtoAndPieceLogsAsJsonString(theUser);

        model.addAttribute("piecesJson", piecesJson);

        return "piecesJson"; // a Thymeleaf template for displaying JSON
    }

    @GetMapping("/admin")
    public String showAdmin() {

        return "admin";
    }

    // utility methods
    @ModelAttribute("trainingTimes")
    public List<String> getTrainingTimes() {
        // Return a list of available training times
        return Arrays.asList("0.5 hour", "1 hour", "1.5 hours", "2 hours", "2.5 hours", "3 hours",
                "3.5 hours", "4 hours", "4.5 hours", "5 hours", "5.5 hours", "6 hours");
    }

    @ModelAttribute("hasPlansForToday")
    public boolean hasPlansForToday(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName();
            User theUser = userService.findUserByEmail(userEmail);

            if (theUser != null) {
                return !planService.getPlansByUserAndDate(theUser).isEmpty();
            }
        }

        // Default to false if the user is not authenticated or any issue occurs.
        return false;
    }

    public String getHoursAsMinutes(String trainingTime) {
        return TrainingTimeUtil.convertHoursToMinutes(trainingTime);
    }

    @GetMapping("/sendEmail")
    public String sendTestEmail(Authentication authentication) {
        String userEmail = authentication.getName();
        User theUser = userService.findUserByEmail(userEmail);

        // Replace these with actual email details
        String to = "astarin0998@gmail.com";
        String subject = "Test Email";
        String body = "This is a test email from Musician Trainer.";

        // Send the email
        emailService.sendNewMail(to, subject, body);

        return "redirect:/home"; // Redirect back to the home page after sending the email
    }

    @NotNull
    private List<PlanPiece> assignPlanItemsToPiecesAndPlan(List<PlanItem> planItems, Plan newPlan) {
        List<PlanPiece> planPieces = new ArrayList<>();

        for (PlanItem planItem : planItems) {
            // Create a new PlanPiece for each PlanItem
            PlanPiece planPiece = new PlanPiece();
            planPiece.setMinutes(planItem.getTime());

            // Retrieve the Piece based on the PlanItem's ID
            Piece existingPiece = pieceService.getPieceById(planItem.getId());

            // Set the Plan, Piece, and add PlanPiece to the list
            planPiece.setPlan(newPlan);
            planPiece.setPiece(existingPiece);
            planPieces.add(planPiece);
        }
        return planPieces;
    }

    @NotNull
    private static Plan createPlanInstance(String convertedTime, User theUser) {
        Plan newPlan = new Plan();

        newPlan.setDate(LocalDate.now()); // Set the date to the current date
        newPlan.setTotalMinutes(Integer.parseInt(convertedTime));
        newPlan.setUser(theUser);

        return newPlan;
    }

    private List<PlanItem> decodeJSON(String jsonString) {

        // print to check what AI generated
        System.out.println("Here the jsonString enters the function: " + jsonString);

        PlanItems thePlanItems;

        try {
            thePlanItems = objectMapper.readValue(jsonString, PlanItems.class);

            // print to check what objectMapper read and deserealized
            System.out.println("Here the objectMapper mapped the json to PlanItems class: " + thePlanItems.getPlanItems());


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return thePlanItems.getPlanItems();
    }


}
