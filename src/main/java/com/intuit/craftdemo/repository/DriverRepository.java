package com.intuit.craftdemo.repository;

import com.intuit.craftdemo.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends MongoRepository<Driver, Integer> {

    List<Driver> findAll();

    Driver findByEmail(String email);
}
