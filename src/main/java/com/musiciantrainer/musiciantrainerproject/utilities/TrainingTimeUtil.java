package com.musiciantrainer.musiciantrainerproject.utilities;

public class TrainingTimeUtil {

    public static String convertHoursToMinutes(String trainingTimeInMinutes) {
        switch (trainingTimeInMinutes) {
            case "0.5 hour":
                return "30 minutes";
            case "1 hour":
                return "60 minutes";
            case "1.5 hours":
                return "90 minutes";
            case "2 hours":
                return "120 minutes";
            case "2.5 hours":
                return "150 minutes";
            case "3 hours":
                return "180 minutes";
            case "3.5 hours":
                return "210 minutes";
            case "4 hours":
                return "240 minutes";
            case "4.5 hours":
                return "270 minutes";
            case "5 hours":
                return "300 minutes";
            case "5.5 hours":
                return "330 minutes";
            case "6 hours":
                return "360 minutes";
            default:
                return "unknown";
        }
    }
}
