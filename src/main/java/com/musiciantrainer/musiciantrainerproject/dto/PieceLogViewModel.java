package com.musiciantrainer.musiciantrainerproject.dto;

import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PieceLogViewModel {

    private List<PieceLog> pieceLogs;

}
