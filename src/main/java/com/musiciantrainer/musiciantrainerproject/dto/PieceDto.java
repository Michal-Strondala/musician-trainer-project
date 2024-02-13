package com.musiciantrainer.musiciantrainerproject.dto;

import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class PieceDto {

    private Long id;
    private String name;
    private String composer;
    private Short priority;
    private Integer time;
    private List<PieceLogDto> pieceLogs;
    private String formattedLastTrainingDate;
    private long numberOfDaysPassed;
    private String numberOfTimesTrained;

    // Constructor to map Piece entity to PieceDto
    public PieceDto(Long id, String name, String composer, Short priority, Integer time,
                    String formattedLastTrainingDate, long numberOfDaysPassed, String numberOfTimesTrained) {
        this.id = id;
        this.name = name;
        this.composer = composer;
        this.priority = priority;
        this.time = time;
        this.formattedLastTrainingDate = formattedLastTrainingDate;
        this.numberOfDaysPassed = numberOfDaysPassed;
        this.numberOfTimesTrained = numberOfTimesTrained;
    }

    public PieceDto(Long id, String name, String composer, Short priority, Integer time, List<PieceLogDto> pieceLogs,
                    String formattedLastTrainingDate, long numberOfDaysPassed, String numberOfTimesTrained) {
        this.id = id;
        this.name = name;
        this.composer = composer;
        this.priority = priority;
        this.time = time;
        this.pieceLogs = pieceLogs;
        this.formattedLastTrainingDate = formattedLastTrainingDate;
        this.numberOfDaysPassed = numberOfDaysPassed;
        this.numberOfTimesTrained = numberOfTimesTrained;
    }

    public PieceDto(String name, String composer, Short priority, Integer time, List<PieceLogDto> pieceLogs,
                    String formattedLastTrainingDate, long numberOfDaysPassed, String numberOfTimesTrained) {
        this.name = name;
        this.composer = composer;
        this.priority = priority;
        this.time = time;
        this.pieceLogs = pieceLogs;
        this.formattedLastTrainingDate = formattedLastTrainingDate;
        this.numberOfDaysPassed = numberOfDaysPassed;
        this.numberOfTimesTrained = numberOfTimesTrained;
    }
}
