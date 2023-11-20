package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.dto.PieceDto;
import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import org.springframework.stereotype.Service;

@Service
public class PieceConversionServiceImpl implements PieceConversionService{
    @Override
    public PieceDto convertToDto(Piece thePiece) {

        PieceDto pieceDto = new PieceDto();
        pieceDto.setId(thePiece.getId());
        pieceDto.setName(thePiece.getName());
        pieceDto.setComposer(thePiece.getComposer());
        pieceDto.setPriority(thePiece.getPriority());
        pieceDto.setFormattedLastTrainingDate(thePiece.getFormattedLastTrainingDate());
        pieceDto.setNumberOfDaysPassed(thePiece.getNumberOfDaysPassed());
        pieceDto.setNumberOfTimesTrained(thePiece.getNumberOfTimesTrained());

        return pieceDto;
    }
}
