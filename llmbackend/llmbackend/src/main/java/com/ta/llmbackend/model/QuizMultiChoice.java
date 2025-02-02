package com.ta.llmbackend.model;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "quiz_multiple_choice", schema = "backend")
@SQLDelete(sql = "UPDATE backend.quiz_multiple_choice SET is_deleted = true WHERE multichoice_id = ?")
@SQLRestriction("is_deleted = false  AND package IS NOT NULL")
public class QuizMultiChoice {

    @Id
    @Column(name = "multichoice_id")
    private UUID multichoiceId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package", referencedColumnName = "package_id")
    @JsonIgnore
    private Package packageMultiChoice;

    @Column(name = "question", columnDefinition = "TEXT")
    private String question;

    @ElementCollection
    @CollectionTable(name = "multichoice_options", joinColumns = @JoinColumn(name = "multichoice_id"))
    @Column(name = "option")
    private List<String> options;

    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}
