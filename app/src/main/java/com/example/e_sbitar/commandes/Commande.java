package com.example.e_sbitar.commandes;

import java.util.HashMap;

public class Commande {

    private String code;
    private String etat, patient;
    private HashMap<String, Object> medsMap;

    public Commande() {
        // Empty constructor required for Firebase
    }

    public Commande(String code, String etat, String patient, HashMap<String, Object> medsMap) {
        this.code = code;
        this.etat = etat;
        this.patient = patient;
        this.medsMap = medsMap;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public HashMap<String, Object> getMedsMap() {
        return medsMap;
    }

    public void setMedsMap(HashMap<String, Object> medsMap) {
        this.medsMap = medsMap;
    }
}
