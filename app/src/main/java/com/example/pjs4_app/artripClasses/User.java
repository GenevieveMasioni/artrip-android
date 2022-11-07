package com.example.pjs4_app.artripClasses;

public class User extends Object {

    private String uid;
    private String pseudo;
    private String mdp;
    private Integer score;

    /**
     * Required empty constructor
     */
    public User() {
        //required empty constructor
    }

    /**
     * Required constructor
     */
    public User(String uid,String pseudo, String mdp, Integer score) {
        this.uid = uid;
        this.pseudo = pseudo;
        this.mdp = mdp;
        this.score = score;
    }

    //Getters to handle access to private attributes
    public Integer getScore(){
        return  score;
}
    public String getPseudo() {
        return pseudo;
    }


}
