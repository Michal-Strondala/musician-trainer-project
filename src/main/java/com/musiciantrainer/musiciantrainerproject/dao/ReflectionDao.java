package com.musiciantrainer.musiciantrainerproject.dao;

import com.musiciantrainer.musiciantrainerproject.entity.Reflection;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReflectionDao extends JpaRepository<Reflection, Long> {

    Reflection findByDateFromAndDateTo(LocalDate dateFrom, LocalDate dateTo);

    List<Reflection> findByUser(User theUser);

    Reflection findByUserAndDateFromAndDateTo(User theUser, LocalDate dateFrom, LocalDate dateTo);
}
