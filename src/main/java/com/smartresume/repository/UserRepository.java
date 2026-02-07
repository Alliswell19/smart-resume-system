// src/main/java/com/smartresume/repository/UserRepository.java
package com.smartresume.repository;

import com.smartresume.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username); // ← 必须存在！
}