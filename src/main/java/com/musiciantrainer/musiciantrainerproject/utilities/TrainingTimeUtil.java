package com.musiciantrainer.musiciantrainerproject.utilities;

public class TrainingTimeUtil {

    public static String convertHoursToMinutes(String trainingTimeInMinutes) {
        switch (trainingTimeInMinutes) {
            case "0.5 hour":
                return "30";
            case "1 hour":
                return "60";
            case "1.5 hours":
                return "90";
            case "2 hours":
                return "120";
            case "2.5 hours":
                return "150";
            case "3 hours":
                return "180";
            case "3.5 hours":
                return "210";
            case "4 hours":
                return "240";
            case "4.5 hours":
                return "270";
            case "5 hours":
                return "300";
            case "5.5 hours":
                return "330";
            case "6 hours":
                return "360";
            default:
                return "unknown";
        }
    }
}
