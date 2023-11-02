package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.User;

import java.util.List;

public interface PieceService  {
    List<Piece> getAllPieces(); // Získání seznamu všech skladeb
    Piece addPiece(Piece thePiece, User theUser); // Vytvoření nové skladby
    void deletePiece(Long theId); // Smazání skladby
    void editPiece(Piece editedPiece, User theUser); // Aktualizace skladby
    List<Piece> getPiecesByUser(User theUser);
    Piece getPieceById(Long theId);

}
