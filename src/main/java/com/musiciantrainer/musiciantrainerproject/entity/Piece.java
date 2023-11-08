package com.musiciantrainer.musiciantrainerproject.entity;

import com.musiciantrainer.musiciantrainerproject.utilities.PriorityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Transient
    private long numberOfDaysPassed;



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
        if (pieceLogs != null && !pieceLogs.isEmpty()) {
            // Sort the pieceLogs list by date in descending order
            pieceLogs.sort(Comparator.comparing(PieceLog::getDate).reversed());
            // Get the first element (most recent training log) from the sorted list
            PieceLog mostRecentLog = pieceLogs.get(0);
            return mostRecentLog.getDate();
        }
        return null; // No training date available
    }

    public String getFormattedLastTrainingDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate lastTrainingDate = getLastTrainingDate();
        if (lastTrainingDate != null) {
            return lastTrainingDate.format(formatter);
        } else {
            return "No training date yet";
        }
    }

    public long getNumberOfDaysPassed() {
        LocalDate today = LocalDate.now();
        LocalDate lastTrainingDate = getLastTrainingDate();
        if (lastTrainingDate != null) {
            return lastTrainingDate.until(today, ChronoUnit.DAYS);
        }
        return 0;
    }

    public String getNumberOfTimesTrained() {
        if (pieceLogs != null && !pieceLogs.isEmpty()) {
            return pieceLogs.size() + "x";
        } else {
            return "Not recorded yet";
        }
    }

}
