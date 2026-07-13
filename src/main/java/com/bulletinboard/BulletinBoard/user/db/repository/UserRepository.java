package com.bulletinboard.BulletinBoard.user.db.repository;

import com.bulletinboard.BulletinBoard.user.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.role='USER' AND u.name = :name")
    List<User> findByName(@Param("name") String name);
    @Query("SELECT u FROM User u WHERE u.role='USER' AND u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    Optional<User> findByLogin(String login);
    @Query("SELECT u FROM User u WHERE u.role='USER'")
    List<User> findAllUsers();
}
