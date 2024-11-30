package com.ta.llmbackend.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "quiz_activities", schema = "backend")
@SQLDelete(sql = "UPDATE backend.quiz_activities SET is_deleted = true WHERE quiz_id = ?")
@SQLRestriction("is_deleted = false AND package IS NOT NULL")
public class QuizActivities {

    @Id
    @Column(name = "quiz_id")
    private UUID quizId = UUID.randomUUID();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users", referencedColumnName = "user_id")
    @JsonIgnoreProperties("listQuizActivites")
    private Users user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package", referencedColumnName = "package_id")
    @JsonIgnoreProperties("listQuizActivities")
    private Package packageQuizActivities;

    @OneToMany(mappedBy = "quizActivities", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Evaluation> evaluationList;

    @Column(name = "final_score")
    private float finalScore;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

}
