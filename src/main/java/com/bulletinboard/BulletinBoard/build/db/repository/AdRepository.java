package com.bulletinboard.BulletinBoard.build.db.repository;

import com.bulletinboard.BulletinBoard.build.db.entity.Ad;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    @Query("SELECT a FROM Ad a JOIN FETCH a.user WHERE a.status='ACTIVE'")
    List<Ad> findAllForUser();

    @Query("SELECT a FROM Ad a JOIN FETCH a.user WHERE a.name = :name")
    List<Ad> findByName(String name);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT a FROM Ad a WHERE a.status='ACTIVE' " +
            "AND a.status='DEACTIVATED' " +
            "AND a.user.id=:userId")
    List<Ad> findByUserId(@Param("userId") Long userId);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT a FROM Ad a WHERE a.status='ACTIVE' " +
            "AND a.user.login=:login")
    List<Ad> findByUserLogin(@Param("login") String login);

    @EntityGraph(attributePaths = {"user"})
    List<Ad> findByNameAndUserLogin(String title, String login);
}
