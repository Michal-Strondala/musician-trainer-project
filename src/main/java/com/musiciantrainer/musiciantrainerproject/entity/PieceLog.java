package com.musiciantrainer.musiciantrainerproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @Column(name = "date_time")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateTime;

    private String note;

    // We do not include CascadeType.REMOVE, because we do not want to delete a Piece when deleting a PieceLog and vice versa
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "piece_id")
    private Piece piece;


    public PieceLog(LocalDateTime dateTime, String note) {
        this.dateTime = dateTime;
        this.note = note;
    }
}
