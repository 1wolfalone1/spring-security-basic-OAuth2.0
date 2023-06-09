package com.wolfalone.springsecuritybasic.repository;


import com.wolfalone.springsecuritybasic.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepo extends JpaRepository<User, Long> {

    User findByEmail(String email);
}
