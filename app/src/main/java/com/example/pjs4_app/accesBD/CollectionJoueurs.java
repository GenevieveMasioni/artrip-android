package com.example.pjs4_app.accesBD;

import com.example.pjs4_app.artripClasses.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CollectionJoueurs extends Object {

    /**
     * Access to all the database's users
     * @return entry point to the CardCollection
     */
    public static CollectionReference getJoueursCollection(){
        return FirebaseFirestore.getInstance().collection("CollectionJoueurs");
    }

    /**
     * Executes following task : creates a user with the following params
     * @param uid
     * @param pseudo
     * @param mdp
     * @return task result
     */
    public static Task<Void> createUser(String uid,String pseudo, String mdp) {
        User userToCreate = new User(uid,pseudo, mdp, 0);
        return CollectionJoueurs.getJoueursCollection().document(uid).set(userToCreate);
    }

    /**
     * Execute following task : fetch the user with the following param
     * @param uid
     * @return task result
     */
    public static Task<DocumentSnapshot> getUser(String uid){
        return CollectionJoueurs.getJoueursCollection().document(uid).get();
    }
}
