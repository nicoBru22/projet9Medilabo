package com.medilabo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.model.Patient;

@Repository
public interface IPatientRepository extends MongoRepository<Patient, String> {
}
