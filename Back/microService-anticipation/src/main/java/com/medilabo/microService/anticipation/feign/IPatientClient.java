package com.medilabo.microService.anticipation.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.medilabo.microService.anticipation.model.Patient;


@FeignClient(name = "patient-service", url = "http://api-patient:8081")
public interface IPatientClient {
	
	@GetMapping("/patient/infos/{id}")
	Patient getPatientById(@PathVariable String id);

    @GetMapping("/patient/infos/{id}/age")
    int getAgePatient(@PathVariable String id);
}
