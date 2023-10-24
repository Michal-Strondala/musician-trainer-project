package com.musiciantrainer.musiciantrainerproject.dao;

import com.musiciantrainer.musiciantrainerproject.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDaoImpl implements UserDao{

    private EntityManager entityManager;

    @Autowired
    public UserDaoImpl(EntityManager theEntityManager) {
        this.entityManager = theEntityManager;
    }


    @Override
    public User findUserByEmail(String theEmail) {
        // retrieve/read from database using email - that User in query is the actual name of the entity, that means it is case sensitive
        TypedQuery<User> theQuery = entityManager.createQuery("FROM User WHERE email=:uEmail", User.class);
        theQuery.setParameter("uEmail", theEmail);

        User theUser = null;
        try {
            theUser = theQuery.getSingleResult();
        } catch (Exception e) {
            theUser = null;
        }

        return theUser;
    }

    @Override
    @Transactional
    public void save(User theUser) {
        // create the user
        entityManager.merge(theUser);
    }

}
