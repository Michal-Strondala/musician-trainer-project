package com.musiciantrainer.musiciantrainerproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "persistent_logins")
public class RememberMeToken {

    @Id
    private String series;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String token;

    @Column(name = "last_used", nullable = false)
    private Date lastUsed;
}
