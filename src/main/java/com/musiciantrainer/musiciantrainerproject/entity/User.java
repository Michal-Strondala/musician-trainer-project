package com.musiciantrainer.musiciantrainerproject.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Collection;

@Entity
@Table(name = "user")
public class User {

    // define fields
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    @Column(name="username")
    private String userName;
    @Column(name="email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "created")
    private LocalDateTime created;
    @Column(name = "logged")
    private LocalDateTime logged;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;


    // define constructors
    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String userName, String email, String password, Collection<Role> roles) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    // define getters/setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLogged() {
        return logged;
    }

    public void setLogged(LocalDateTime logged) {
        this.logged = logged;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }


    // define toString method

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", created=" + created +
                ", logged=" + logged +
                ", roles=" + roles +
                '}';
    }


    // define convenient method
}
