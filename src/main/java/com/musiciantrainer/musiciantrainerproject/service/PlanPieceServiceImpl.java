package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.dao.PlanPieceDao;
import com.musiciantrainer.musiciantrainerproject.entity.PlanPiece;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanPieceServiceImpl implements PlanPieceService {

    private PlanPieceDao planPieceDao;

    @Autowired
    public PlanPieceServiceImpl(PlanPieceDao planPieceDao) {
        this.planPieceDao = planPieceDao;
    }

    @Override
    public void addPlanPiece(PlanPiece planPiece) {
        planPieceDao.save(planPiece);
    }
}
