package com.ptit.rikkei_bank.repository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.ptit.rikkei_bank.dto.response.TransactionResponse;
import com.ptit.rikkei_bank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT new com.ptit.rikkei_bank.dto.response.TransactionResponse(t.id, t.transactionCode, t.amount, t.description, t.createdAt, t.fromAccount.accountNumber, t.toAccount.accountNumber) " +
           "FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId ORDER BY t.createdAt DESC")
    Page<TransactionResponse> findByAccountProjected(@Param("accountId") Long accountId, Pageable pageable);

    @EntityGraph(attributePaths = {"fromAccount", "toAccount"})
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.accountNumber = :fromAccountNumber OR t.toAccount.accountNumber = :toAccountNumber ORDER BY t.createdAt DESC")
    List<Transaction> findByFromAccountAccountNumberOrToAccountAccountNumberOrderByCreatedAtDesc(@Param("fromAccountNumber") String fromAccountNumber, @Param("toAccountNumber") String toAccountNumber);
    boolean existsByTransactionCode(String transactionCode);
}
