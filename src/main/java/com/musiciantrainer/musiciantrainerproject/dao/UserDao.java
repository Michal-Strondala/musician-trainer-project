package com.musiciantrainer.musiciantrainerproject.dao;

import com.musiciantrainer.musiciantrainerproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    //User findByUserName(String userName);

    // Maybe I should test this if it ignores cases (maybe it should be findUserByEmailIgnoreCase)
    User findUserByEmail(String email);
}
