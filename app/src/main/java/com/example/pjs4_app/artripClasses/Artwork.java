package com.example.pjs4_app.artripClasses;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

public class Artwork extends Object {
    private String nom;
    private String fiche;
    private GeoPoint localisation;

    /**
     * Required empty constructor
     */
    public Artwork(){

    }

    /**
     * Required constructor
     */
    public Artwork(String name, String artWorkCardID, GeoPoint localisation){
        //required constructor
        this.nom = name;
        this.fiche = artWorkCardID;
        this.localisation = localisation;
    }

    /**
     * Converts GeoPoint to LatLng
     * @return LatLng object
     */
    public com.google.android.gms.maps.model.LatLng position(){
        return new LatLng(this.getLocalisation().getLatitude(), this.getLocalisation()
                .getLongitude());
    }


    //Getters to handle access to private attributes
    public String getNom() {
        return nom;
    }
    public String getFiche() {
        return fiche;
    }
    public GeoPoint getLocalisation() {
        return localisation;
    }

}
