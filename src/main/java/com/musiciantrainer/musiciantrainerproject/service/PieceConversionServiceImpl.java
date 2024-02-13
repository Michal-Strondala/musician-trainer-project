package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.dto.PieceDto;
import com.musiciantrainer.musiciantrainerproject.dto.PieceLogDto;
import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PieceConversionServiceImpl implements PieceConversionService{
    @Override
    public PieceDto convertToDto(Piece thePiece) {

        PieceDto pieceDto = new PieceDto();

        pieceDto.setId(thePiece.getId());
        pieceDto.setName(thePiece.getName());
        pieceDto.setComposer(thePiece.getComposer());
        pieceDto.setPriority(thePiece.getPriority());
        pieceDto.setTime(thePiece.getTime());
        pieceDto.setFormattedLastTrainingDate(thePiece.getFormattedLastTrainingDate());
        pieceDto.setNumberOfDaysPassed(thePiece.getNumberOfDaysPassed());
        pieceDto.setNumberOfTimesTrained(thePiece.getNumberOfTimesTrained());

        return pieceDto;
    }

    @Override
    public PieceDto convertToDtoIncludingPieceLogs(Piece thePiece) {
        PieceDto pieceDto = new PieceDto();

        pieceDto.setId(thePiece.getId());
        pieceDto.setName(thePiece.getName());
        pieceDto.setComposer(thePiece.getComposer());
        pieceDto.setPriority(thePiece.getPriority());
        pieceDto.setTime(thePiece.getTime());

        // Ensure pieceLogs are initialized and set
        List<PieceLogDto> pieceLogDtos = thePiece.getPieceLogs().stream()
                .map(this::convertToPieceLogDto) // Method to convert PieceLog to PieceLogDto
                .collect(Collectors.toList());
        pieceDto.setPieceLogs(pieceLogDtos);

        pieceDto.setFormattedLastTrainingDate(thePiece.getFormattedLastTrainingDate());
        pieceDto.setNumberOfDaysPassed(thePiece.getNumberOfDaysPassed());
        pieceDto.setNumberOfTimesTrained(thePiece.getNumberOfTimesTrained());
        return pieceDto;
    }

    private PieceLogDto convertToPieceLogDto(PieceLog thePieceLog) {
        // Implement conversion from PieceLog to PieceLogDto
        PieceLogDto pieceLogDto = new PieceLogDto();

        pieceLogDto.setId(thePieceLog.getId());
        pieceLogDto.setDate(thePieceLog.getDate());
        pieceLogDto.setNote(thePieceLog.getNote());

        return pieceLogDto;
    }
}
