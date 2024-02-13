package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.entity.Reflection;
import com.musiciantrainer.musiciantrainerproject.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface ReflectionService {
    Reflection saveReflection(Reflection newReflection);

    Reflection getReflectionByDateFromAndDateTo(LocalDate dateFrom, LocalDate dateTo);

    Reflection getReflectionByUserAndDateFromAndDateTo(User theUser, LocalDate dateFrom, LocalDate dateTo);

    List<Reflection> getReflectionsByUserOrderedByDateFromAndDateTo(User theUser);

    Reflection getReflectionById(Long reflectionId);

    void deleteReflection(Long reflectionId);
}
