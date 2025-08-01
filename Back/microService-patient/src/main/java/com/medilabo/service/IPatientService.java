package com.medilabo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.medilabo.model.Patient;
import com.medilabo.model.Transmission;

public interface IPatientService {
	List<Patient> getAllPatient();
	Patient getPatientById(String id);
	void deletePatient(String id);
	Patient addPatient(Patient patient);
	Optional<Patient> updatePatient(Patient patient);
	int agePatient(LocalDate dateNaissance);
	Transmission addTransmission(Transmission newTransmission, String patientid);
	List<Transmission> getAllTransmissionOfPatient(String patientId);
}
