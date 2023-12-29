package com.musiciantrainer.musiciantrainerproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@ToString
@Entity // Entity is declare to make this class an object for the database
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {

    // define fields
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(length=68)
    private String password;

    // The @CreationtTimestamp is a convenient annotation that sets the field value to the current timestamp when the entity is first saved.
    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.DATE) // This will override and make column name created with specific DATE format
    private LocalDate created;

    // Logged zatím nikde neřeším
    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime logged;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Piece> pieces;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Plan> plans;

    private boolean enabled;


    public User() {
        super();
        this.enabled = false;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, Collection<Role> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    // add a convenient methods for bi-directional relationship

    public void add(Plan tempPlan) {

        if (plans == null) {
            plans = new ArrayList<>();
        }

        plans.add(tempPlan);

        tempPlan.setUser(this);
    }
}
