package com.medilabo.microService.anticipation.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.medilabo.microService.anticipation.model.Transmission;

@FeignClient(name = "transmission-service", url = "http://api-transmissions:8081")
public interface ITransmissionClient {
    
	@GetMapping("/transmission/getTransmissionsOfPatient")
	List<Transmission> getAllTransmissionOfPatient(@RequestParam String patientId);
    
    
}
