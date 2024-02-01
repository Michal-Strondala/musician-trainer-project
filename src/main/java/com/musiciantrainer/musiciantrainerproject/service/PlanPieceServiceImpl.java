package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.dao.PlanPieceDao;
import com.musiciantrainer.musiciantrainerproject.entity.Piece;
import com.musiciantrainer.musiciantrainerproject.entity.PlanPiece;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

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

    @Override
    public PlanPiece getPlanPieceById(Long id) {
        return planPieceDao.findById(id).orElse(null);
    }

}
