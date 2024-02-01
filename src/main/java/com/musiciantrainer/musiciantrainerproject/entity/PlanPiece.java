package com.musiciantrainer.musiciantrainerproject.entity;

import com.musiciantrainer.musiciantrainerproject.utilities.DateUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Optional;

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


    public PlanPiece(Plan plan, Piece piece, Integer minutes) {
        this.plan = plan;
        this.piece = piece;
        this.minutes = minutes;
    }

    public void setPiece(Piece piece) {
        if (this.piece != piece) {
            this.piece = piece;
            if (piece != null) {
                piece.getPlanPieces().add(this);
            }
        }
    }

    public boolean isPieceRecordedToday() {
        return DateUtil.isRecordedToday(this.piece.getPieceLogs());
    }

}
