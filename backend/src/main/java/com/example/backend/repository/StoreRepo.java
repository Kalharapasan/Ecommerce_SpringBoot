package com.example.backend.repository;

import com.example.backend.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepo extends JpaRepository<Store, Integer> {
    Optional<Store> findByOwnerUserId(Integer userId);
    Optional<Store> findByOwnerUsername(String username);
}
