package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.entity.Plan;
import com.musiciantrainer.musiciantrainerproject.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface PlanService {


    Plan getPlanById(Long id);

    Plan addPlan(Plan newPlan);

    Plan getPlanByTotalMinutesAndDate(Integer totalMinutes, LocalDate date);

    List<Plan> getPlansByUserOrderedByTotalMinutes(User theUser);

    List<Plan> getPlansByUserAndDate(User theUser);

    void deletePlan(Long planId);
}
