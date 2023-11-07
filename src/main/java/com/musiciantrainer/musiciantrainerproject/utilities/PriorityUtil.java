package com.musiciantrainer.musiciantrainerproject.utilities;

public class PriorityUtil {
    public static String convertPriorityToString(Short priority) {
        switch (priority) {
            case 0:
                return "none";
            case 1:
                return "low";
            case 2:
                return "medium";
            case 3:
                return "high";
            default:
                return "unknown";
        }
    }
}
