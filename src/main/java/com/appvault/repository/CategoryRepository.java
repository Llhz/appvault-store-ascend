package com.appvault.repository;

import com.appvault.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c.name, COUNT(a) FROM Category c LEFT JOIN c.apps a GROUP BY c.id, c.name ORDER BY COUNT(a) DESC")
    List<Object[]> countAppsByCategory();
}
