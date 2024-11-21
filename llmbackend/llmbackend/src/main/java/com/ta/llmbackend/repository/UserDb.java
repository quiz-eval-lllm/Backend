package com.ta.llmbackend.repository;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ta.llmbackend.model.User;
import java.util.List;

@Repository
public interface UserDb extends JpaRepository<User, UUID> {

    List<User> findAll(Sort sort);

    List<User> findByRole(int role, Sort sort);
}
