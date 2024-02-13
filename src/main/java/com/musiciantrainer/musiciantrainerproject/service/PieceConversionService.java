package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.dto.PieceDto;
import com.musiciantrainer.musiciantrainerproject.entity.Piece;

public interface PieceConversionService {

    PieceDto convertToDto(Piece thePiece);

    PieceDto convertToDtoIncludingPieceLogs(Piece thePiece);
}
