package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.dao.PlanDao;
import com.musiciantrainer.musiciantrainerproject.entity.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PlanServiceImpl implements PlanService {

    private PlanDao planDao;

    @Autowired
    public PlanServiceImpl(PlanDao planDao) {
        this.planDao = planDao;
    }


    @Override
    public Plan addPlan(Plan newPlan) {
        return planDao.save(newPlan);
    }

    @Override
    public Plan getPlanByTotalMinutesAndDate(Integer totalMinutes, LocalDate date) {
        return planDao.findByTotalMinutesAndDate(totalMinutes, LocalDate.now());
    }

    @Override
    public Plan getPlanById(Long id) {
        return planDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Did not find plan with id - " + id));
    }

}
