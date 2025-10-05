package com.example.repository;

import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.userId = :id")
    User findByUser_id(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.role = 'external'")
    List<User> findExternalTeachers();

    @Query("SELECT u FROM User u WHERE u.role = 'internal' or u.isChairman=true")
    List<User> findInternalTeachers();
}
