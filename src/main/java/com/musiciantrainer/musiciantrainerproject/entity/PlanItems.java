package com.musiciantrainer.musiciantrainerproject.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class PlanItems {

    private List<PlanItem> planItems;

    public PlanItems(List<PlanItem> planItems) {
        this.planItems = planItems;
    }
}
