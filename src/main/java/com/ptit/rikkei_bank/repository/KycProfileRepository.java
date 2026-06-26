package com.ptit.rikkei_bank.repository;

import com.ptit.rikkei_bank.entity.KycProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycProfileRepository extends JpaRepository<KycProfile, Long> {
}
