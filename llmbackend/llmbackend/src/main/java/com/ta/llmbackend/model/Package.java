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

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
@Table(name = "package", schema = "backend")
@SQLDelete(sql = "UPDATE backend.package SET is_deleted = true WHERE package_id = ?")
@SQLRestriction("is_deleted = false AND users IS NOT NULL")
public class Package {

    @Id
    @Column(name = "package_id", nullable = false, unique = true)
    private UUID packageId = UUID.randomUUID();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "users", referencedColumnName = "user_id")
    @JsonIgnoreProperties("listPackage")
    private Users creator;

    @Column(name = "module")
    private String module;

    @Column(name = "subject")
    private String subject;

    @Column(name = "lang")
    private String language;

    @Column(name = "type")
    private int type;

    @Column(name = "prompt")
    private String prompt;

    @Column(name = "context")
    private String context;

    @OneToMany(mappedBy = "packageEssay", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<QuizEssay> listQuizEssay;

    @OneToMany(mappedBy = "packageMultiChoice", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<QuizMultiChoice> listQuizMultiChoice;

    @OneToMany(mappedBy = "packageQuizActivities", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<QuizActivities> listQuizActivities;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

}
