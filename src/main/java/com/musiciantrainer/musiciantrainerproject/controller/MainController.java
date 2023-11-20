package com.musiciantrainer.musiciantrainerproject.controller;

import com.musiciantrainer.musiciantrainerproject.entity.HomePageViewModel;
import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.service.PieceService;
import com.musiciantrainer.musiciantrainerproject.service.UserService;
import com.musiciantrainer.musiciantrainerproject.utilities.PriorityUtil;
import com.musiciantrainer.musiciantrainerproject.utilities.TrainingTimeUtil;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Controller
public class MainController {

    @Value("${openai.model}")
    private String aiModel;
    @Value("${openai.api.key}")
    private String apiKey;



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

    // Figure out the dropdown menu - it still does not work
    // Then I have to figure out how to take the generated plan which is in JSON and deserealize it to custom schedule table.
    // Adjust HTML page - like navbar and so on and buttons and so on.
    @GetMapping("/myPlan")
    public String showMyPlan(@RequestParam("trainingTime") String trainingTime, Model model, Authentication authentication) {

        String userEmail = authentication.getName();
        User theUser = userService.findUserByEmail(userEmail);

        //zalozeni objektu openai
        OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(90));

        //system prompt
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "I want you to act as my music learning planner. \n" +
                "You will help user to organize his daily routine plan of training music. \n" +
                "You will decide what is the most important to train today. \n" +
                "I will give you a list of pieces in JSON format. You will do user's schedule for the particular time.\n" +
                "The user will give you his time for that day in minutes and/or hours.\n" +
                "You will have priority in the list. \n" +
                "The bigger the value of priority, the bigger the priority, and a low value is a low priority. \n" +
                "A 0 priority is a piece without priority.\n" +
                "Important is also \"formattedLastTrainingDate\", it is the date when I practiced the exercise the last time. \n" +
                "You can find the attribute \"numberOfDaysPassed\" which displays the number of days since the last training date. \n" +
                "You can find there \"numberOfTimesTrained\" which shows the number of times I trained that piece.\n" +
                "The goal is to not lose the memory of pieces. Each train session has to have at least 30 minutes (1 pomodoro). \n" +
                "One hour is 2 pomodoros. \n" +
                "\n" +
                "Write in proper English.\n" +
                "\n" +
                "The output should be in JSON style with time schedule included.\n" +
                "\n" +
                "The pattern should be always like this:\n" +
                "{\n" +
                "  \"schedule\": [\n" +
                "    {\n" +
                "      \"time\": \"30 mins\",\n" +
                "      \"piece\": {\n" +
                "        \"id\": 5,\n" +
                "        \"name\": \"piece2\",\n" +
                "        \"composer\": \"composer1\",\n" +
                "        \"priority\": \"3\"\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"time\": \"30 mins\",\n" +
                "      \"piece\": {\n" +
                "        \"id\": 9,\n" +
                "        \"name\": \"piece5\",\n" +
                "        \"composer\": \"compose3\",\n" +
                "        \"priority\": \"2\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "\n" +
                "Do not include \"details.\" \n" +
                "Do not write any extra text in the end.");
        messages.add(systemMessage);

        //user prompt
        System.out.print("First Query: ");
        //Scanner scanner = new Scanner(System.in);
        //String time = String.valueOf(120);
        String convertedTime = getHoursAsMinutes(trainingTime);
        String userPrompt = pieceService.getPiecesDtoAsJsonString(theUser) + " My time for today is " + convertedTime + ".";
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

        model.addAttribute("myplan", responseMessage);

        service.shutdownExecutor();


        return "myplan";
    }

    @GetMapping("/piecesToJson")
    public String piecesToJson(Model model, Authentication authentication) {
        String userEmail = authentication.getName();
        User theUser = userService.findUserByEmail(userEmail);

        String piecesJson = pieceService.getPiecesDtoAsJsonString(theUser);

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
    public String getHoursAsMinutes(String trainingTime) {
        return TrainingTimeUtil.convertHoursToMinutes(trainingTime);
    }



}
