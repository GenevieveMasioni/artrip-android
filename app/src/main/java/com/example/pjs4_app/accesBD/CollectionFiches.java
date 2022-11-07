package com.example.pjs4_app.accesBD;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CollectionFiches extends Object {

    /**
     * Access to all the database's cards
     * @return entry point to the CardCollection
     */
    public static CollectionReference getFichesCollection(){
        return FirebaseFirestore.getInstance().collection("CollectionFiches");
    }

    /**
     * Executes following task : fetch the card with the following param
     * @param id
     * @return task result
     */
    public static Task<DocumentSnapshot> getFiche(String id){
        return CollectionFiches.getFichesCollection().document(id).get();
    }
}
