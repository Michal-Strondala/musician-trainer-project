package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;
import com.musiciantrainer.musiciantrainerproject.entity.User;

import java.util.List;

public interface PieceService  {
    List<Piece> getAllPieces(); // Získání seznamu všech skladeb
    Piece addPiece(Piece thePiece, User theUser); // Vytvoření nové skladby
    void deletePiece(Long theId); // Smazání skladby
    void editPiece(Piece editedPiece, User theUser); // Aktualizace skladby
    List<Piece> getPiecesByUserOrderedByPriorityAndDaysPassed(User theUser);

    List<PieceLog> getPieceLogsByPieceIdOrderedByDate(Long pieceId);

    Piece getPieceById(Long theId);
    String getPiecesDtoAsJsonString(User theUser);
    boolean checkIfPieceIsNotNull(Long id);


}
