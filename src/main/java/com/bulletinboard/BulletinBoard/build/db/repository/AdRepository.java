package com.bulletinboard.BulletinBoard.build.db.repository;

import com.bulletinboard.BulletinBoard.build.db.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    @Query("SELECT a FROM Ad a WHERE a.status='ACTIVE'")
    List<Ad> findAllForUser();
    @Query("SELECT a FROM Ad a WHERE a.status='ACTIVE' AND a.status='DEACTIVATED'")
    List<Ad> findAllByStatus();
    List<Ad> findByName(String name);
    @Query("SELECT a FROM Ad a WHERE a.status='ACTIVE' AND a.user.id=:userId")
    List<Ad> findByUserId(@Param("userId") Long userId);
    @Query("SELECT a FROM Ad a WHERE a.status='ACTIVE' AND a.user.login=:login")
    List<Ad> findByUserLogin(@Param("login") String login);
    Optional<Ad> findByNameAndUserLogin(String title, String login);
}
