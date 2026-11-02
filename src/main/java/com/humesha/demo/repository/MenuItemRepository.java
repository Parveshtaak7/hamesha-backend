package com.humesha.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.humesha.demo.model.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    List<MenuItem> findByCategory(String category);

    // Fetch active menu items
    List<MenuItem> findByIsTrashFalse();

    // Fetch deleted items currently in the trash
    List<MenuItem> findByIsTrashTrue();

    @Modifying
    @Transactional
    @Query("UPDATE MenuItem m SET m.category = :newName WHERE m.category = :oldName")
    void updateCategoryAcrossAllItems(@Param("oldName") String oldName, @Param("newName") String newName);
}