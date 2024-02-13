package com.musiciantrainer.musiciantrainerproject.dao;

import com.musiciantrainer.musiciantrainerproject.entity.PieceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PieceLogDao extends JpaRepository<PieceLog, Long> {

    List<PieceLog> findAllByDateBetween(LocalDate dateStart, LocalDate dateEnd);

}
