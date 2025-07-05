package com.medilabo.microService.transmission.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.microService.transmission.model.Transmission;

@Repository
public interface ITransmissionRepository extends MongoRepository<Transmission, String> {
	List<Transmission> findAllByPatientId(String patientId);
}
