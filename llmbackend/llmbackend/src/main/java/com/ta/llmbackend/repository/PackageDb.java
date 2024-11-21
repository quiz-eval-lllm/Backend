package com.ta.llmbackend.repository;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ta.llmbackend.model.Package;
import java.util.List;

@Repository
public interface PackageDb extends JpaRepository<Package, UUID> {

    List<Package> findAll(Sort sort);
}
