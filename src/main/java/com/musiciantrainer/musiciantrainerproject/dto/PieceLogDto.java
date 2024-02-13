package com.musiciantrainer.musiciantrainerproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class PieceLogDto {

    private Long id;
    private LocalDate date;
    private String note;

    public PieceLogDto(Long id, LocalDate date, String note) {
        this.id = id;
        this.date = date;
        this.note = note;
    }

    public PieceLogDto(LocalDate date, String note) {
        this.date = date;
        this.note = note;
    }
}
