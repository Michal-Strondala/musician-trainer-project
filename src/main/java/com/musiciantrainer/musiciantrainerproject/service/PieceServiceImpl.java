package com.musiciantrainer.musiciantrainerproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musiciantrainer.musiciantrainerproject.dao.PieceDao;
import com.musiciantrainer.musiciantrainerproject.dao.PieceLogDao;
import com.musiciantrainer.musiciantrainerproject.dto.PieceDto;
import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PieceServiceImpl implements PieceService{

    private PieceConversionService pieceConversionService;
    private PieceDao pieceDao;
    private PieceLogDao pieceLogDao;
    private ObjectMapper objectMapper;

    @Autowired
    public PieceServiceImpl(PieceConversionService pieceConversionService, PieceDao pieceDao, PieceLogDao pieceLogDao, ObjectMapper objectMapper) {
        this.pieceConversionService = pieceConversionService;
        this.pieceDao = pieceDao;
        this.pieceLogDao = pieceLogDao;
        this.objectMapper = objectMapper;
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
    public List<Piece> getPiecesByUserOrderedByPriorityAndDaysPassed(User theUser) {

        List<Piece> pieces = pieceDao.findByUser(theUser);
        pieces.sort(Comparator.comparing(Piece::getPriority).reversed()
                .thenComparing(Piece::getNumberOfDaysPassed, Comparator.reverseOrder())
                .thenComparing(Piece::getNumberOfTimesTrained));
        return pieces;
    }

    @Override
    public List<Piece> getPiecesByUser(User theUser) {

        return pieceDao.findByUser(theUser);
    }

    @Override
    public List<PieceLog> getPieceLogsByPieceIdOrderedByDate(Long pieceId) {
        Piece piece = getPieceById(pieceId);

        if (piece != null) {
            List<PieceLog> pieceLogs = piece.getPieceLogs();
            pieceLogs.sort(Comparator.comparing(PieceLog::getDate, Comparator.reverseOrder()));
            return pieceLogs;
        }
        return Collections.emptyList();
    }

    @Override
    public List<PieceLog> getPieceLogsByUserOrderedByDate(User theUser) {
        List<Piece> pieceList = getPiecesByUser(theUser);
        List<PieceLog> pieceLogs = new ArrayList<>();

        if (!pieceList.isEmpty()) {
            for (Piece thePiece : pieceList) {
                pieceLogs.addAll(thePiece.getPieceLogs());
            }

            pieceLogs.sort(Comparator.comparing(PieceLog::getDate, Comparator.reverseOrder()));
            return pieceLogs;
        }
        return Collections.emptyList();
    }

    @Override
    public Piece getPieceById(Long theId) {
        Optional<Piece> result = pieceDao.findById(theId);

        Piece thePiece;

        if (result.isPresent()) {
            thePiece = result.get();
        }
        else {
            // we didn't find the piece
            throw new RuntimeException("Did not find piece with id - " + theId);
        }

        return thePiece;
    }


    @Override
    public String getPiecesDtoAsJsonString(User theUser) {
        List<Piece> pieces = pieceDao.findByUser(theUser);
        pieces.sort(Comparator.comparing(Piece::getPriority).reversed()
                .thenComparing(Piece::getNumberOfDaysPassed, Comparator.reverseOrder())
                .thenComparing(Piece::getNumberOfTimesTrained));

        List<PieceDto> pieceDtos = pieces.stream()
                .map(pieceConversionService::convertToDto)
                .collect(Collectors.toList());

        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pieceDtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting pieces to JSON", e);
        }
    }

    @Override
    public boolean checkIfPieceIsNotNull(Long id) {
        Optional<Piece> thePiece = pieceDao.findById(id);
        if (thePiece.isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public PieceLog getPieceLogById(Long pieceLogId) {
        return pieceLogDao.findById(pieceLogId).orElse(null);
    }

    @Override
    public void editPieceLog(PieceLog editedPieceLog) {
        pieceLogDao.save(editedPieceLog);
    }

    @Override
    public void deletePieceLog(Long pieceLogId) {
        pieceLogDao.deleteById(pieceLogId);
    }
}
