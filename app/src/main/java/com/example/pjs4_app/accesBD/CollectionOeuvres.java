package com.example.pjs4_app.accesBD;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CollectionOeuvres extends Object {
    /**
     * Access to all the database's artworks
     * @return entry point to the ArtworksCollection
     */
    public static CollectionReference getOeuvresCollection(){
        return FirebaseFirestore.getInstance().collection("CollectionOeuvres");
    }

    /**
     * Executes following task : Fetch the artwork with the following param
     * @param id
     * @return task result
     */
    public static Task<DocumentSnapshot> getOeuvre(String id){
        return CollectionOeuvres.getOeuvresCollection().document(id).get();
    }
}
