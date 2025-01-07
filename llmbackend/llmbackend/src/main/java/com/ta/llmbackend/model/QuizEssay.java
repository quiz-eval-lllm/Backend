package com.ta.llmbackend.model;

import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "quiz_essay", schema = "backend")
@SQLDelete(sql = "UPDATE backend.quiz_essay SET is_deleted = true WHERE essay_id = ?")
@SQLRestriction("is_deleted = false AND package IS NOT NULL")
public class QuizEssay {

    @Id
    @Column(name = "essay_id")
    private UUID essayId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package", referencedColumnName = "package_id")
    @JsonIgnore
    private Package packageEssay;

    @Column(name = "question", columnDefinition = "TEXT")
    private String question;

    @Column(name = "context", columnDefinition = "TEXT")
    private String context;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}
