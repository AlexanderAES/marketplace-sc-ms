package com.alexandersuetnov.userserviceapp.repository;

import com.alexandersuetnov.userserviceapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User>findUserByEmail(String email);
    Optional<User>findUserById(Long id);
    User findUserIdByEmail(String email);
    Optional<User> findUserByUsername(String username);
    User findUserByActivationCode(String code);
}
