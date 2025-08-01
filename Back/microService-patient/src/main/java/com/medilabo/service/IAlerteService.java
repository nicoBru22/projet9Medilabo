package com.medilabo.service;

public interface IAlerteService {
	
	String riskEvaluation (String patientId);
	boolean isBordline(long occurence, int age);
	boolean isInDanger(long occurence, int age, String genre);
	boolean isEarlyOnset(long occurence, int age, String genre);

}
