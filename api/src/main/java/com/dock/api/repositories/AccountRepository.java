package com.dock.api.repositories;

import com.dock.api.models.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountModel, Long> {
    boolean existsByPersonId(Long personId);
}