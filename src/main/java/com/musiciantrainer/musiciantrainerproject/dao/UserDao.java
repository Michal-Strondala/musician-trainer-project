package com.musiciantrainer.musiciantrainerproject.dao;

import com.musiciantrainer.musiciantrainerproject.entity.User;

public interface UserDao {
    //User findByUserName(String userName);
    void save(User theUser);

    User findUserByEmail(String email);
}
