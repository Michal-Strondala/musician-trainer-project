package com.musiciantrainer.musiciantrainerproject.dao;

import com.musiciantrainer.musiciantrainerproject.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PlanDao extends JpaRepository<Plan, Long>  {

    Plan findByTotalMinutesAndDate(Integer totalMinutes, LocalDate date);
}
