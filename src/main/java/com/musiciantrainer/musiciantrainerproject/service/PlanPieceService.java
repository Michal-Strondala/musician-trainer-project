package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.entity.PlanPiece;

public interface PlanPieceService {
    void addPlanPiece(PlanPiece planPiece);

    PlanPiece getPlanPieceById(Long id);

}
