package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.dao.PieceDao;
import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PieceServiceImpl implements PieceService{

    private PieceDao pieceDao;

    @Autowired
    public PieceServiceImpl(PieceDao pieceDao) {
        this.pieceDao = pieceDao;
    }

    @Override
    public List<Piece> getAllPieces() {
        return pieceDao.findAll();
    }

    @Override
    public Piece addPiece(Piece thePiece, User theUser) {

        Piece piece = new Piece(thePiece.getName(), thePiece.getComposer(), thePiece.getPriority());

        piece.setUser(theUser);

        return pieceDao.save(piece); // saving piece into the database
    }

    @Override
    public void deletePiece(Long theId) {

        pieceDao.deleteById(theId);
    }

    @Override
    public void editPiece(Piece editedPiece, User theUser) {

        editedPiece.setUser(theUser);

        pieceDao.save(editedPiece);
    }

    @Override
    public List<Piece> getPiecesByUser(User theUser) {
        return pieceDao.findByUser(theUser);
    }

    @Override
    public Piece getPieceById(Long theId) {
        Optional<Piece> result = pieceDao.findById(theId);

        Piece thePiece = null;

        if (result.isPresent()) {
            thePiece = result.get();
        }
        else {
            // we didn't find the piece
            throw new RuntimeException("Did not find piece with id - " + theId);
        }

        return thePiece;
    }

}
