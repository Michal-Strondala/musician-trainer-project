package com.musiciantrainer.musiciantrainerproject.entity;

import com.musiciantrainer.musiciantrainerproject.utilities.DateUtil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "piece_log")
public class PieceLog {

    // Vytvoření piecelogu bude nejspíš v tabulce, kterou vygeneruje AI v planu,
    // u každé piece bude možnost zaevidovat piecelog, že se to splnilo.

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

    public String getFormattedRecordDate() {
        return DateUtil.createFormattedRecordDate(this.date);
    }

}
