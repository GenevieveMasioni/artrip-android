package com.example.pjs4_app.artripClasses;

import java.util.ArrayList;
import java.util.Map;

public class Card extends Object {
    private String artisteAlias;
    private String artisteBio;
    private Map<String, String> artisteReseaux;
    private ArrayList<String> couleursOeuvre;
    private String descriptionOeuvre;
    private String dimensions;
    private String indiceOeuvre;
    private String labelOeuvre;
    private String lieuOeuvre;
    private String nomArtiste;
    private String nomOfficielOeuvre;
    private String nomUsageOeuvre;

    /**
     * Required empty constructor
     */
    public Card(){

    }

    /**
     * Required constructor
     */
    public Card(String artisteAlias, String artisteBio, Map<String, String> artisteReseaux,
                ArrayList<String> couleursOeuvre, String descriptionOeuvre, String dimensions,
                String indiceOeuvre, String labelOeuvre, String lieuOeuvre, String nomArtiste,
                String nomOffcielOeuvre,String nomUsageOeuvre){
        this.artisteAlias = artisteAlias;
        this.artisteBio = artisteBio;
        this.artisteReseaux =artisteReseaux;
        this.couleursOeuvre = couleursOeuvre;
        this.descriptionOeuvre = descriptionOeuvre;
        this.indiceOeuvre = indiceOeuvre;
        this.labelOeuvre = labelOeuvre;
        this.lieuOeuvre = lieuOeuvre;
        this.nomArtiste = nomArtiste;
        this.nomOfficielOeuvre = nomOffcielOeuvre;
        this.nomUsageOeuvre = nomUsageOeuvre;
        this.dimensions = dimensions;
    }

    /**
     * Formats Artwork information
     * @return formatted Artwork information
     */
    public String toStringOeuvre(){
        String s="";
        s+= "Nom Officiel : " + this.getNomOfficielOeuvre() + "\n \n" +
                "Nom d'usage : " + this.getNomUsageOeuvre() + "\n \n" +
                "Description : " + this.getDescriptionOeuvre() + "\n \n" +
                "Dimensions : "+ this.getDimensions() + "\n \n" +
                " Lieu : " + this.getLieuOeuvre() + "\n" ;
        return s;
    }

    /**
     * Formats Artist information
     * @return formatted Artist information
     */
    public String toStringArtiste(){
        String s="";
        s+= "Nom : " + this.getNomArtiste() + "\n \n" +
                "Alias : " + this.getArtisteAlias() + "\n \n" +
                "Biographie : " + this.getArtisteBio() + "\n";
        return s;
    }

    //Getters to handle access to private attributes
    public String getArtisteAlias() {
        return artisteAlias;
    }
    public String getArtisteBio() {
        return artisteBio;
    }
    public Map<String, String>getArtisteReseaux() {
        return artisteReseaux;
    }
    public ArrayList<String> getCouleursOeuvre() {
        return couleursOeuvre;
    }
    public String getDescriptionOeuvre() {
        return descriptionOeuvre;
    }
    public String getIndiceOeuvre() {
        return indiceOeuvre;
    }
    public String getDimensions() {return dimensions;}
    public String getLabelOeuvre() {
        return labelOeuvre;
    }
    public String getLieuOeuvre() {
        return lieuOeuvre;
    }
    public String getNomArtiste() {
        return nomArtiste;
    }
    public String getNomOfficielOeuvre() {
        return nomOfficielOeuvre;
    }
    public String getNomUsageOeuvre() {
        return nomUsageOeuvre;
    }
}
