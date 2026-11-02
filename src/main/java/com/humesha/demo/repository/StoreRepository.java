package com.humesha.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.humesha.demo.model.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}