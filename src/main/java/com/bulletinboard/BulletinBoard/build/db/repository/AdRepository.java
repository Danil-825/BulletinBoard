package com.bulletinboard.BulletinBoard.build.db.repository;

import com.bulletinboard.BulletinBoard.build.db.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {

}
