package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;
import com.musiciantrainer.musiciantrainerproject.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface PieceService  {
    List<Piece> getAllPieces(); // Získání seznamu všech skladeb
    Piece addPiece(Piece thePiece, User theUser); // Vytvoření nové skladby
    void deletePiece(Long theId); // Smazání skladby
    void editPiece(Piece editedPiece, User theUser); // Aktualizace skladby
    List<Piece> getPiecesByUserOrderedByPriorityAndDaysPassed(User theUser);

    List<Piece> getPiecesByUser(User theUser);

    List<PieceLog> getPieceLogsByPieceIdOrderedByDate(Long pieceId);

    List<PieceLog> getPieceLogsByUserOrderedByDate(User theUser);

    Piece getPieceById(Long theId);
    String getPiecesDtoAsJsonString(User theUser);

    String getPiecesDtoAndPieceLogsAsJsonStringInDateRange(User theUser, LocalDate dateFrom, LocalDate dateTo);

    String getPiecesDtoAndPieceLogsAsJsonString(User theUser);

    boolean checkIfPieceIsNotNull(Long id);


    PieceLog getPieceLogById(Long pieceLogId);

    void editPieceLog(PieceLog editedPieceLog);

    void deletePieceLog(Long pieceLogId);
}
