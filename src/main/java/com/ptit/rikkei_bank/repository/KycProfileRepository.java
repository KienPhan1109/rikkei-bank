package com.ptit.rikkei_bank.repository;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;

import com.ptit.rikkei_bank.dto.response.KycResponse;
import com.ptit.rikkei_bank.entity.KycProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KycProfileRepository extends JpaRepository<KycProfile, Long> {
    Optional<KycProfile> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    boolean existsByIdNumber(String idNumber);

    @EntityGraph(attributePaths = {"user"})
    List<KycProfile> findAll();

    @Query("SELECT new com.ptit.rikkei_bank.dto.response.KycResponse(k.id, k.idNumber, k.fullName, k.dob, k.sex, k.address, k.idCardFrontUrl, k.status, k.verifiedAt, k.createdAt, k.user.id) FROM KycProfile k")
    Page<KycResponse> findAllProjected(Pageable pageable);
}
