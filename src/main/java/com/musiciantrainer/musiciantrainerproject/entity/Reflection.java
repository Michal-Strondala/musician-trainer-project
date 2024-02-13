package com.musiciantrainer.musiciantrainerproject.entity;

import com.musiciantrainer.musiciantrainerproject.utilities.DateUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "reflection")
public class Reflection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dateFrom;
    private LocalDate dateTo;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String reflectionText;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Reflection(LocalDate dateFrom, LocalDate dateTo, String reflectionText, User user) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.reflectionText = reflectionText;
        this.user = user;
    }

    public Reflection(LocalDate dateFrom, LocalDate dateTo, User user) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.user = user;
    }

//    public String getFormattedDateFrom() {
//        return DateUtil.createFormattedDateFrom(this.dateFrom);
//    }
//    public String getFormattedDateTo() {
//        return DateUtil.createFormattedDateTo(this.dateTo);
//    }
}
