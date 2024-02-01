package com.musiciantrainer.musiciantrainerproject.dto;

import com.musiciantrainer.musiciantrainerproject.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class CreatedPlansViewModel {

    private List<Plan> createdPlans;
}
