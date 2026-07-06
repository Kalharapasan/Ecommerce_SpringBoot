package com.example.backend.repository;

import com.example.backend.model.StoreReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreReviewRepo extends JpaRepository<StoreReview, Integer> {
    List<StoreReview> findByStoreStoreIdOrderByCreatedDateDesc(Integer storeId);
    
    @Query("SELECT AVG(CAST(r.rating as double)) FROM StoreReview r WHERE r.store.storeId = :storeId")
    Double getAverageRatingForStore(@Param("storeId") Integer storeId);
}
