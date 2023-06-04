package com.example.e_sbitar;

public class Patient {
    String nom_prénom, dateNaissance, adresse, id, numTel, email;

    public Patient(){

    }

    public Patient(String vNom_prénom, String vDateNaissance, String vAdresse, String vId, String vNumTel, String vEmail){
        this.nom_prénom = vNom_prénom;
        this.dateNaissance = vDateNaissance;
        this.adresse = vAdresse;
        this.id = vId;
        this.numTel = vNumTel;
        this.email = vEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom_prénom() {
        return nom_prénom;
    }

    public void setNom_prénom(String nom_prénom) {
        this.nom_prénom = nom_prénom;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}