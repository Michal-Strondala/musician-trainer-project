package com.musiciantrainer.musiciantrainerproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "plan_piece")
public class PlanPiece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "piece_id")
    private Piece piece;

    private Integer minutes;

    private boolean isDone;

    public PlanPiece(Plan plan, Piece piece, Integer minutes, boolean isDone) {
        this.plan = plan;
        this.piece = piece;
        this.minutes = minutes;
        this.isDone = isDone;
    }
}
