package com.musiciantrainer.musiciantrainerproject.service;

import com.musiciantrainer.musiciantrainerproject.dao.RoleDao;
import com.musiciantrainer.musiciantrainerproject.dao.UserDao;
import com.musiciantrainer.musiciantrainerproject.entity.Role;
import com.musiciantrainer.musiciantrainerproject.entity.User;
import com.musiciantrainer.musiciantrainerproject.user.WebUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Service
public class UserServiceImpl implements UserService{

    private UserDao userDao;

    private RoleDao roleDao;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, RoleDao roleDao, BCryptPasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findUserByEmail(String email) {
        // check the database if the user already exists
        return userDao.findUserByEmail(email);
    }

    @Override
    public void save(WebUser webUser) {
        User user = new User();

        // assign user details to the user object
        user.setUserName(webUser.getUserName());
        user.setPassword(passwordEncoder.encode(webUser.getPassword()));
        user.setEmail(webUser.getEmail());

        // give user default role of "USER"
        user.setRoles(Arrays.asList(roleDao.findRoleByName("ROLE_USER")));

        // save user in the database
        userDao.save(user);
    }


    // This is inherited method from UserDetails interface, which
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userDao.findUserByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("Invalid email or password.");
        }

        Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                authorities);
    }



    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role tempRole : roles) {
            SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getName());
            authorities.add(tempAuthority);
        }

        return authorities;
    }

}
