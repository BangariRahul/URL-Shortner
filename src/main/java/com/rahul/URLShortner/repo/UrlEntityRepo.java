package com.rahul.URLShortner.repo;

import com.rahul.URLShortner.model.UrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlEntityRepo extends JpaRepository<UrlEntity, Long> {




    @Query("SELECT u FROM UrlEntity u WHERE u.orignalUrl = ?1")
    List<UrlEntity> findUrlByOrignalUrl(String orignalUrl);




}
