package com.ta.llmbackend.repository;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ta.llmbackend.model.Users;
import java.util.List;

@Repository
public interface UserDb extends JpaRepository<Users, UUID> {

    List<Users> findAll(Sort sort);

    List<Users> findByRole(int role, Sort sort);
}
