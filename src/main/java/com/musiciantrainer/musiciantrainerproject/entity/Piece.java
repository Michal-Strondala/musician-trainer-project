package com.musiciantrainer.musiciantrainerproject.entity;

import com.musiciantrainer.musiciantrainerproject.utilities.DateUtil;
import com.musiciantrainer.musiciantrainerproject.utilities.PriorityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "piece")
public class Piece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String composer;

    @Min(value = 0, message = "The minimal priority is 0.")
    @Max(value = 3, message = "The maximal priority is 3.")
    private Short priority;

    @OneToMany(mappedBy = "piece",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.PERSIST, CascadeType.REFRESH})
    private List<PieceLog> pieceLogs;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;



    public Piece(String name, String composer, Short priority) {
        this.name = name;
        this.composer = composer;
        this.priority = priority;
    }

    // add a convenient methods for bi-directional relationship

    public void add(PieceLog tempPieceLog) {

        if (pieceLogs == null) {
            pieceLogs = new ArrayList<>();
        }

        pieceLogs.add(tempPieceLog);

        tempPieceLog.setPiece(this);
    }

    // utility methods
    public String getPriorityAsString() {
        return PriorityUtil.convertPriorityToString(this.priority);
    }

    public LocalDate getLastTrainingDate() {
        return DateUtil.findLastTrainingDate(this);
    }


}
