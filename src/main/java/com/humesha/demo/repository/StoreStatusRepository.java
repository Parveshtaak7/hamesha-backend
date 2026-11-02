package com.humesha.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.humesha.demo.model.StoreStatus;

@Repository
public interface StoreStatusRepository extends JpaRepository<StoreStatus, Long> {
}