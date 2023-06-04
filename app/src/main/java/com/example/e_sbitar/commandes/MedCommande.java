package com.example.e_sbitar.commandes;

public class MedCommande {

    private String nom;
    private String prix, quantité;

    public MedCommande() {
        // Empty constructor required for Firebase
    }

    public MedCommande(String nom, String prix, String quantité) {
        this.nom = nom;
        this.prix= prix;
        this.quantité = quantité;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getQuantité() {
        return quantité;
    }

    public void setQuantité(String quantité) {
        this.quantité = quantité;
    }
}