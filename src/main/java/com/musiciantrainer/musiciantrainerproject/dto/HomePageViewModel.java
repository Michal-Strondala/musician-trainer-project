package com.musiciantrainer.musiciantrainerproject.dto;

import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@Getter
@Setter
public class HomePageViewModel {

    private List<Piece> pieces;


}
