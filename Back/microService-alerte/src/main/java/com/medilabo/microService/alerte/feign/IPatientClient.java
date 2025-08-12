package com.medilabo.microService.alerte.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.medilabo.microService.alerte.model.Patient;


@FeignClient(name = "patient-service", url = "http://api-patient:8081")
public interface IPatientClient {
	
	@GetMapping("/patient/infos/{id}")
	Patient getPatientById(@PathVariable Long id);

    @GetMapping("/patient/infos/{id}/age")
    int getAgePatient(@PathVariable Long id);
}
