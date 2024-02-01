package com.musiciantrainer.musiciantrainerproject.entity;

import com.musiciantrainer.musiciantrainerproject.utilities.TrainingTimeUtil;
import jakarta.persistence.*;
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
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private LocalDate date;

    @Column(name = "total_minutes")
    private Integer totalMinutes;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanPiece> planPieces;


    public Plan(LocalDate date, Integer totalMinutes, User user, List<PlanPiece> planPieces) {
        this.date = date;
        this.totalMinutes = totalMinutes;
        this.user = user;
        this.planPieces = planPieces;
    }

    // Convenience method to add PlanPiece
    public void addPlanPiece(PlanPiece planPiece) {
        if (planPieces == null) {
            planPieces = new ArrayList<>();
        }
        planPieces.add(planPiece);
        planPiece.setPlan(this);
    }

    public String getMinutesAsHours(Integer trainingTotalMinutes) {
        String stringMinutes = Integer.toString(trainingTotalMinutes);
        return TrainingTimeUtil.convertMinutesToHours(stringMinutes);
    }


}
