package com.intuit.craftdemo.repository;

import com.intuit.craftdemo.model.Admin;
import com.intuit.craftdemo.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {

    List<Admin> findAll();

    Admin findByEmail(String email);
}
