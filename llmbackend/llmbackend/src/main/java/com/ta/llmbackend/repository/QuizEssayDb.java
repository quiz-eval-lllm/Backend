package com.ta.llmbackend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ta.llmbackend.model.QuizEssay;

@Repository
public interface QuizEssayDb extends JpaRepository<QuizEssay, UUID> {

}
