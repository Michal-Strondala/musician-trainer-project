package com.musiciantrainer.musiciantrainerproject.controller;

import com.musiciantrainer.musiciantrainerproject.dto.CreatedReflectionsViewModel;
import com.musiciantrainer.musiciantrainerproject.dto.ReflectionViewModel;
import com.musiciantrainer.musiciantrainerproject.dto.WebUser;
import com.musiciantrainer.musiciantrainerproject.entity.Reflection;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.service.PieceService;
import com.musiciantrainer.musiciantrainerproject.service.ReflectionService;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/reflection")
public class ReflectionController {


    @Value("${openai.model}")
    private String aiModel;
    @Value("${openai.api.key}")
    private String apiKey;


    private UserService userService;
    private PieceService pieceService;
    private ReflectionService reflectionService;

    @Autowired
    public ReflectionController(UserService userService, PieceService pieceService, ReflectionService reflectionService) {
        this.userService = userService;
        this.pieceService = pieceService;
        this.reflectionService = reflectionService;
    }

    @GetMapping("/showCreateReflectionForm")
    public String showCreateReflectionForm(Model theModel, Authentication authentication) {

        // Get the currently authenticated user's email (username in your case)
        String userEmail = authentication.getName();

        // Get the user from the service based on the email
        User theUser = userService.findUserByEmail(userEmail);

        // Set user in the model to prepopulate the form
        theModel.addAttribute("user", theUser);

        // Add an empty WebUser object to the model for the form
        theModel.addAttribute("webUser", new WebUser());

        // Add an empty Reflection object to the model for the form
        theModel.addAttribute("reflection", new Reflection());

        // Send over to our form
        return "create-reflection";
    }

    // Generate reflection using AI

    @PostMapping("/processReflectionForm")
    public String processReflectionForm(@RequestParam("dateFrom") String stringDateFrom,
                                         @RequestParam("dateTo") String stringDateTo,
                                         Model model, Authentication authentication) {

        String userEmail = authentication.getName();
        User theUser = userService.findUserByEmail(userEmail);

        // Date range
        LocalDate parsedDateFrom = LocalDate.parse(stringDateFrom, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        LocalDate parsedDateTo = LocalDate.parse(stringDateTo, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        Reflection savedReflection;
        OpenAiService service = null;

        // check if the Reflection in the date range exists in the database or not
        if (doesReflectionExist(parsedDateFrom, parsedDateTo, theUser)) {
            savedReflection = reflectionService.getReflectionByUserAndDateFromAndDateTo(theUser, parsedDateFrom, parsedDateTo);
        } else {

            // zalozeni objektu openai
            service = new OpenAiService(apiKey, Duration.ofSeconds(180));

            // system prompt
            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You will act as a music learning mentor. \n" +
                    "You will help user to reflect on his progress through his/her music learning journey. \n" +
                    "You will receive a list of pieces and their pieceLogs, which are records when the user trained particular piece and might even wrote a note. This list will be in JSON format. You will do user's reflection for the particular time range.\n" +
                    "The user will give you his/her time range for that reflection in this format: from dd.MM.yyyy to dd.MM.yyyy. Therefore you will use only the pieces and their piecelogs created within the time range.\n" +
                    "You will also have priority in the list. \n" +
                    "The bigger the value of priority, the bigger the priority, and the lower value is, the lower priority. \n" +
                    "A 0 priority is a piece without priority.\n" +
                    "Important is also \"formattedLastTrainingDate\", it is the date when the user practiced the exercise the last time. \n" +
                    "You can find the attribute \"numberOfDaysPassed\" which displays the number of days since the last training date. \n" +
                    "You can find there \"numberOfTimesTrained\" which shows the number of times the user trained that piece.\n" +
                    "The goal is to create a motivational reflection of the user's progress using his/her notes, number of times the user trained, number of days since the user trained, pieces' priorities and other useful data to create a statistic summary. \n" +
                    "\n" +
                    "The motivational reflection should also contain what you think the user should do better and try to positively motivate the user. Convey it in a polite way.\n" +
                    "\n" +
                    "The output must be a String text. I want the result in Czech language, in a meaningful way, in structured HTML with titles, paragraphs, strong element and a few emojis. Also use some ideas which could help the user with his/her progress. And do not write HTML words like tag or html or header etc.\n");
            messages.add(systemMessage);

            //user prompt
            System.out.print("First Query: ");

            String userPrompt = pieceService.getPiecesDtoAndPieceLogsAsJsonStringInDateRange(theUser, parsedDateFrom, parsedDateTo) + " The selected time range is from " + stringDateFrom + " to " + stringDateTo;
            ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);
            messages.add(userMessage);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model(aiModel)
                    .messages(messages)
                    .n(1)
                    .maxTokens(2000)
                    .build();

            ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();

            service.streamChatCompletion(chatCompletionRequest)
                    .doOnError(Throwable::printStackTrace)
                    .blockingForEach(System.out::println);

            // *** Process AI response ***

            // 1. Create Reflection instance
            Reflection newReflection = new Reflection(parsedDateFrom, parsedDateTo, responseMessage.getContent(), theUser);

            // 2. Save the Reflection to the database
            savedReflection = reflectionService.saveReflection(newReflection);
        }

        ReflectionViewModel reflectionViewModel = new ReflectionViewModel(savedReflection);

        // Add the Reflection to the model
        model.addAttribute("reflectionViewModel", reflectionViewModel);
        model.addAttribute("user", theUser);

        if (service != null) {
            service.shutdownExecutor();
        }

        return "reflection";
    }

    private boolean doesReflectionExist(LocalDate dateFrom, LocalDate dateTo, User theUser) {
        if (theUser == null) {
            return false; // If user is null, immediately return false
        }
        // Attempt to fetch the reflection from the database
        Reflection reflection = reflectionService.getReflectionByUserAndDateFromAndDateTo(theUser, dateFrom, dateTo);
        // Return true if reflection is not null, otherwise false
        return reflection != null;
    }

    @GetMapping("/createdReflections")
    public String showCreatedReflections(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName(); // Get the email from principal
            User theUser = userService.findUserByEmail(userEmail);

            if (theUser != null) {
                List<Reflection> createdReflections = reflectionService.getReflectionsByUserOrderedByDateFromAndDateTo(theUser);
                CreatedReflectionsViewModel theCreatedReflectionsViewModel = new CreatedReflectionsViewModel(createdReflections);

                model.addAttribute("createdReflectionsViewModel", theCreatedReflectionsViewModel);
                model.addAttribute("user", theUser); // Add user to the model

                return "reflection-list"; // This is a Thymeleaf template name
            }
        }

        // The user is not logged in or something else went wrong
        return "redirect:/home";

    }

    @GetMapping("/showReflection")
    public String showReflection(@RequestParam("reflectionId") Long reflectionId,
                                 Model model, Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            String userEmail = authentication.getName(); // Get the email from principal
            User theUser = userService.findUserByEmail(userEmail);

            if (theUser != null) {
                Reflection theReflection = reflectionService.getReflectionById(reflectionId);
                ReflectionViewModel theReflectionViewModel = new ReflectionViewModel(theReflection);

                model.addAttribute("reflectionViewModel", theReflectionViewModel);
                model.addAttribute("user", theUser); // Add user to the model

                return "reflection"; // This is a Thymeleaf template name
            }
        }

        // The user is not logged in or something else went wrong
        return "redirect:/reflection/createdReflections";

    }

    @GetMapping("/deleteReflection")
    public String deleteReflection(@RequestParam("reflectionId") Long reflectionId,
                             RedirectAttributes redirectAttributes) {
        try {
            // Delete the Reflection based on the reflectionId
            reflectionService.deleteReflection(reflectionId);

            // Add a success message to be displayed on the redirected page
            redirectAttributes.addFlashAttribute("successDeleteReflection", true);
        } catch (Exception e) {
            // Handle exceptions, e.g., if the Reflection does not exist

            // Add an error message to be displayed on the redirected page
            redirectAttributes.addFlashAttribute("error", "Failed to delete the reflection.");
        }

        return "redirect:/reflection/createdReflections?recordSuccess"; // Redirect to the appropriate page
    }
}
