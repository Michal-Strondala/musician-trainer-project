package com.musiciantrainer.musiciantrainerproject.dao;

import com.musiciantrainer.musiciantrainerproject.entity.Plan;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlanDao extends JpaRepository<Plan, Long>  {

    Plan findByTotalMinutesAndDate(Integer totalMinutes, LocalDate date);

    List<Plan> findByUserAndDate(User theUser, LocalDate date);
}
