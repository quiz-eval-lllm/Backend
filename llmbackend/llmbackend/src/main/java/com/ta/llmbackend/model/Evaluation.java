package com.ta.llmbackend.model;

import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "evaluation", schema = "backend")
@SQLDelete(sql = "UPDATE backend.evaluation SET is_deleted = true WHERE eval_id = ?")
@SQLRestriction("is_deleted = false")
public class Evaluation {

    @Id
    @Column(name = "eval_id", unique = true)
    private UUID evalId = UUID.randomUUID();

    @Column(name = "question_id")
    private UUID questionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz", referencedColumnName = "quiz_id")
    @JsonIgnoreProperties("evaluationList")
    private QuizActivities quizActivities;

    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;

    @Column(name = "score")
    private float score;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

}
