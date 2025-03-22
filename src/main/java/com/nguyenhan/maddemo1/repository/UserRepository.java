package com.nguyenhan.maddemo1.repository;

import com.nguyenhan.maddemo1.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
//    Optional<User> findByUsername(String username);
    Optional<User> findByVerificationCode(String verificationCode);
}