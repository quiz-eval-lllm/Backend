package com.ta.llmbackend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ta.llmbackend.model.QuizMultiChoice;

@Repository
public interface QuizMultichoiceDb extends JpaRepository<QuizMultiChoice, UUID> {

}
