package com.ta.llmbackend.repository;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ta.llmbackend.model.QuizActivities;
import java.util.List;

@Repository
public interface QuizActivitiesDb extends JpaRepository<QuizActivities, UUID> {

    List<QuizActivities> findAll(Sort sort);
}
