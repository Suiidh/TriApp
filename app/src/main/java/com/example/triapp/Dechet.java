package com.example.triapp;

public class Dechet {
    private String nom;       // Nom du déchet
    private String details;  // Type de poubelle ou informations associées
    private int imageResId;   // Identifiant de l'image ressource

    // Constructeur
    public Dechet(String nom, String details, int imageResId) {
        this.nom = nom;
        this.details = details;
        this.imageResId = imageResId;
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public String getPoubelle() {
        return details;
    }

    public int getImageResId() {
        return imageResId;
    }
}


