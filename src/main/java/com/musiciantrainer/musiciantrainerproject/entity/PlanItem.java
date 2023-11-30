package com.musiciantrainer.musiciantrainerproject.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class PlanItem {

    private Integer time;
    private Long id;

    public PlanItem(Integer time, Long id) {
        this.time = time;
        this.id = id;
    }
}
