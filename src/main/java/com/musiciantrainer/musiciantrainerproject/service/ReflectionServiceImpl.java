package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.dao.ReflectionDao;
import com.musiciantrainer.musiciantrainerproject.entity.Reflection;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class ReflectionServiceImpl implements ReflectionService {
    private ReflectionDao reflectionDao;
    @Autowired
    public ReflectionServiceImpl(ReflectionDao reflectionDao) {
        this.reflectionDao = reflectionDao;
    }



    @Override
    public Reflection saveReflection(Reflection newReflection) {
        return reflectionDao.save(newReflection);
    }

    @Override
    public Reflection getReflectionByDateFromAndDateTo(LocalDate dateFrom, LocalDate dateTo) {
        return reflectionDao.findByDateFromAndDateTo(dateFrom, dateTo);
    }

    @Override
    public Reflection getReflectionByUserAndDateFromAndDateTo(User theUser, LocalDate dateFrom, LocalDate dateTo) {
        return reflectionDao.findByUserAndDateFromAndDateTo(theUser, dateFrom, dateTo);
    }

    @Override
    public List<Reflection> getReflectionsByUserOrderedByDateFromAndDateTo(User theUser) {

        List<Reflection> createdReflections = reflectionDao.findByUser(theUser);
        createdReflections.sort(Comparator.comparing(Reflection::getDateFrom).thenComparing(Reflection::getDateTo));
        return createdReflections;
    }

    @Override
    public Reflection getReflectionById(Long reflectionId) {
        return reflectionDao.findById(reflectionId).orElse(null);
    }

    @Override
    public void deleteReflection(Long reflectionId) {
        reflectionDao.deleteById(reflectionId);
    }
}
