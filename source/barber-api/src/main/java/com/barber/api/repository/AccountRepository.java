package com.barber.api.repository;

import com.barber.api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Account findAccountByUsername(String username);
    Account findAccountByEmail(String email);
    Account findAccountByPhone(String phone);

    Boolean existsByUsername(String username);

    long countByGroupId(Long id);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);
}
