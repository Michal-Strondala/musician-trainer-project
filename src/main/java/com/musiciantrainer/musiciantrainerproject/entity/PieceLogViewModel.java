package com.musiciantrainer.musiciantrainerproject.entity;

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
