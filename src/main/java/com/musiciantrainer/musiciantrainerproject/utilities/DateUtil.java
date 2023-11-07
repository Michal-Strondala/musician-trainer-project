package com.musiciantrainer.musiciantrainerproject.utilities;

import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DateUtil {

    public static LocalDate findLastTrainingDate(Piece thePiece) {
        List<PieceLog> pieceLogs = thePiece.getPieceLogs();
        List<LocalDate> dates = new ArrayList<>();
        if (pieceLogs != null && !pieceLogs.isEmpty()) {
            for (PieceLog pieceLog : pieceLogs) {
                dates.add(pieceLog.getDate());
            }
            LocalDate latest = Collections.max(dates);
            return latest;
        }
        return null; // No training date available
    }
}
