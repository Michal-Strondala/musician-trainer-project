package com.musiciantrainer.musiciantrainerproject.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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


}
