package com.example.backend.repository;

import com.example.backend.model.StoreFollower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreFollowerRepo extends JpaRepository<StoreFollower, Integer> {
    Optional<StoreFollower> findByUserUserIdAndStoreStoreId(Integer userId, Integer storeId);
    Integer countByStoreStoreId(Integer storeId);
    Boolean existsByUserUserIdAndStoreStoreId(Integer userId, Integer storeId);
}
