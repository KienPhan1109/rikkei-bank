package com.ptit.rikkei_bank.repository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import com.ptit.rikkei_bank.dto.response.AccountResponse;
import com.ptit.rikkei_bank.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByUserId(Long userId);
    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT new com.ptit.rikkei_bank.dto.response.AccountResponse(a.id, a.accountNumber, a.balance, a.currency, a.active, a.createdAt, a.updatedAt, a.user.id) FROM Account a WHERE a.user.id = :userId")
    Page<AccountResponse> findByUserIdProjected(Long userId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);
}
