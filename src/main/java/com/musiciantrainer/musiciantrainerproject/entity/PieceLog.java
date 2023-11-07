package com.musiciantrainer.musiciantrainerproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "piece_log")
public class PieceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private LocalDate date;

    private String note;

    // We do not include CascadeType.REMOVE, because we do not want to delete a Piece when deleting a PieceLog and vice versa
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "piece_id")
    private Piece piece;


    public PieceLog(LocalDate date, String note) {
        this.date = date;
        this.note = note;
    }
}
