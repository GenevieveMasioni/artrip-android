package com.example.pjs4_app.accesBD;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CollectionArtiste extends Object {

    /**
     * Access to All the database's artists
     * @return entry point to the ArtistCollection
     */
    public static CollectionReference getArtistesCollection(){
        return FirebaseFirestore.getInstance().collection("CollectionArtistes");
    }

}
