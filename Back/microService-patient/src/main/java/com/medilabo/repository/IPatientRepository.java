package com.medilabo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medilabo.model.Patient;

/**
 * Interface de persistance pour les entités {@link Patient}.
 * 
 * Cette interface étend {@link MongoRepository} afin de bénéficier automatiquement
 * des opérations CRUD (Create, Read, Update, Delete) sur la collection MongoDB associée aux patients.
 * 
 * Le type de l'entité est {@code Patient}, et le type de sa clé primaire est {@code String}.
 */
@Repository
public interface IPatientRepository extends JpaRepository<Patient, Long> {
}
