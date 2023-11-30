package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.entity.Plan;

import java.time.LocalDate;

public interface PlanService {


    Plan getPlanById(Long id);

    Plan addPlan(Plan newPlan);

    Plan getPlanByTotalMinutesAndDate(Integer totalMinutes, LocalDate date);
}
