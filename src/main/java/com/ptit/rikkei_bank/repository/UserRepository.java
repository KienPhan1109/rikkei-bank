package com.ptit.rikkei_bank.repository;

import com.ptit.rikkei_bank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ptit.rikkei_bank.dto.response.UserResponse;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByPhoneNumberAndIsDeletedFalse(String phoneNumber);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT new com.ptit.rikkei_bank.dto.response.UserResponse(u.id, u.username, u.email, u.phoneNumber, u.role.name, u.isKyc, u.isActive, u.createdAt) FROM User u WHERE u.isDeleted = false")
    Page<UserResponse> findAllProjected(Pageable pageable);
}
