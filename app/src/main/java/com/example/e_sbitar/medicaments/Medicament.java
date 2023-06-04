package com.example.e_sbitar.medicaments;

public class Medicament {

    String code, nom, prix, date_de_fabrication, date_dexpiration, quantité;

    public Medicament(){

    }

    public Medicament(String vNom, String vCode, String vPrix, String vDateFab, String vDateExp, String vQuantité){
        this.nom = vNom;
        this.code = vCode;
        this.prix = vPrix;
        this.date_de_fabrication = vDateFab;
        this.date_dexpiration = vDateExp;
        this.quantité = vQuantité;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getDate_de_fabrication() {
        return date_de_fabrication;
    }

    public void setDate_de_fabrication(String date_de_fabrication) { this.date_de_fabrication = date_de_fabrication; }

    public String getDate_dexpiration() {
        return date_dexpiration;
    }

    public void setDate_dexpiration(String date_dexpiration) { this.date_dexpiration = date_dexpiration; }

    public String getNom(){
        return nom;
    }

    public void setNom(String nom){
        this.nom = nom;
    }

    public String getQuantité(){
        return quantité;
    }

    public void setQuantité(String quantité) {
        this.quantité = quantité;
    }
}