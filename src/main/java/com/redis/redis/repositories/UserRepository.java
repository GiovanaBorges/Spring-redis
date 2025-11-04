package com.redis.redis.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.redis.redis.models.User;

@Repository
public interface UserRepository extends MongoRepository<User,String>{
    User findByEmail(String email);
    Optional<User> findById(String id);
}
