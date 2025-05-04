package com.CPUScheduling.CPUScheduling.repository;

import com.CPUScheduling.CPUScheduling.entities.User;
import com.mongodb.client.MongoDatabase;
import org.springframework.data.mongodb.core.MongoAdminOperations;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByUsername(String username);

}
