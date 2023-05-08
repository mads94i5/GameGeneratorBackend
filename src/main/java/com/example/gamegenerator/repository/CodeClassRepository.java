package com.example.gamegenerator.repository;

import com.example.gamegenerator.entity.CodeClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeClassRepository extends JpaRepository<CodeClass, Long> {
}
