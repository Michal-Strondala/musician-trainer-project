package com.musiciantrainer.musiciantrainerproject.utilities;

import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

public class DateUtil {

    public static LocalDate calculateLastTrainingDate(List<PieceLog> pieceLogs) {
        if (pieceLogs != null && !pieceLogs.isEmpty()) {
            // Sort the pieceLogs list by date in descending order
            pieceLogs.sort(Comparator.comparing(PieceLog::getDate).reversed());
            // Get the first element (most recent training log) from the sorted list
            PieceLog mostRecentLog = pieceLogs.get(0);
            return mostRecentLog.getDate();
        }
        return null; // No training date available
    }

    public static String calculateFormattedLastTrainingDate(List<PieceLog> pieceLogs) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate lastTrainingDate = calculateLastTrainingDate(pieceLogs);
        if (lastTrainingDate != null) {
            return lastTrainingDate.format(formatter);
        } else {
            return "No training date yet";
        }
    }

    public static String createFormattedRecordDate(LocalDate theDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        if (theDate != null) {
            return theDate.format(formatter);
        } else {
            return "No record date yet";
        }
    }

    public static long calculateNumberOfDaysPassed(List<PieceLog> pieceLogs) {
        LocalDate today = LocalDate.now();
        LocalDate lastTrainingDate = calculateLastTrainingDate(pieceLogs);
        if (lastTrainingDate != null) {
            return lastTrainingDate.until(today, ChronoUnit.DAYS);
        }
        return 0;
    }

    public static String calculateNumberOfTimesTrained(List<PieceLog> pieceLogs) {
        if (pieceLogs != null && !pieceLogs.isEmpty()) {
            return pieceLogs.size() + "x";
        } else {
            return "Not recorded yet";
        }
    }
}
