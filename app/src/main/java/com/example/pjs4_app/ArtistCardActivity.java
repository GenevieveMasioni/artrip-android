package com.example.pjs4_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pjs4_app.accesBD.CollectionFiches;
import com.example.pjs4_app.accesBD.CollectionOeuvres;
import com.example.pjs4_app.artripClasses.Card;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ArtistCardActivity extends AppCompatActivity {
    final FirebaseStorage storage = FirebaseStorage.getInstance();
    final StorageReference storageReference = storage.getReference();

    private boolean bundelMap, bundleSieste, bundleArchivesFragment;

    TextView infoOeuvre;
    TextView titreOeuvre;
    ImageView image;
    TextView infoArtiste;

    ImageButton fb;
    ImageButton ig;
    ImageButton twit;
    ImageButton web;
    ImageButton yt;

    private ArrayList<LatLng> parcours;
    private LatLng goodMarker;

    /**
     * Creates the activity, displays it and sets the attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_card);

        bundelMap = getIntent().getExtras().getBoolean("bundleMap");
        bundleSieste = getIntent().getExtras().getBoolean("bundleSieste");
        bundleArchivesFragment = getIntent().getExtras().getBoolean("bundleArchivesFragment");

        String nomOeuvre = getIntent().getStringExtra("nomOeuvre");

        final Button closeFiche = findViewById(R.id.closeFicheButton);
        final Intent ScanActivity = new Intent(this, CameraActivity.class);

        infoOeuvre = findViewById(R.id.DescriptionOeuvre);
        titreOeuvre = findViewById(R.id.TitreFiche);
        image = findViewById(R.id.imageOeuvre);
        infoArtiste = findViewById(R.id.DescriptionArtiste);

        fb = findViewById(R.id.FacebookButton);
        ig = findViewById(R.id.InstagramButton);
        twit = findViewById(R.id.TwitterButton);
        web = findViewById(R.id.WebsiteButton);
        yt = findViewById(R.id.YoutubeButton);

        parcours = (ArrayList<LatLng>) getIntent().getExtras().get("positions");
        goodMarker = (LatLng) getIntent().getExtras().get("goodMarker");

        printFiche(nomOeuvre);

        /**
         * Different action for closeFiche button according to the previous activity
         */
        closeFiche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bundelMap) {
                    Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                    i.putExtra("positions", parcours);
                    i.putExtra("goodMarker", goodMarker);
                    startActivity(i);
                    finish();
                }else if(bundleSieste){
                    startActivity(new Intent(getApplicationContext(), SleepActivity.class));
                    finish();
                }else if(bundleArchivesFragment){
                    Intent i = new Intent(getApplicationContext(), UserActivity.class);
                    i.putExtra("positions", parcours);
                    startActivity(i);
                }
                else
                 startActivity(ScanActivity);
            }
        });
    }

    /**
     * Sets a Card display
     * @param nom
     */
    public void printFiche(final String nom){
        CollectionOeuvres.getOeuvre(nom).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    final DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        CollectionFiches.getFiche(doc.get("fiche").toString())
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot resp = task.getResult();
                                    if(resp.exists()){
                                        Card fiche = resp.toObject(Card.class);
                                        infoOeuvre.setText(fiche.toStringOeuvre());
                                        titreOeuvre.setText(fiche.getNomOfficielOeuvre());
                                        infoArtiste.setText(fiche.toStringArtiste());
                                        toStringArtisteRes(fb, ig, twit, web, yt,
                                                fiche.getArtisteReseaux());
                                        GlideApp.with(ArtistCardActivity.this)
                                                .load(storageReference
                                                        .child("imagesPrincipalesOeuvres/"
                                                                + nom + ".jpg"))
                                                .into(image);
                                        showChildren();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * Puts back the visibility of the needed text view to VISIBLE
     */
    private void showChildren(){
        findViewById(R.id.TitreFiche).setVisibility(View.VISIBLE);
        findViewById(R.id.imageOeuvre).setVisibility(View.VISIBLE);
        findViewById(R.id.scrollView_artistCardActivity).setVisibility(View.VISIBLE);

    }

    /**
     * Displays the Card's artist social media according to the clicked button
     * @param fb
     * @param ig
     * @param twit
     * @param web
     * @param yt
     * @param artistReseaux
     */
    public void toStringArtisteRes(ImageButton fb, ImageButton ig, ImageButton twit,
                                   ImageButton web, ImageButton yt,
                                   final Map<String, String> artistReseaux){
        if(artistReseaux.get("facebook").equals("")){
            fb.setVisibility(View.GONE);
        }else{
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriUrl = Uri.parse("https://www.facebook.com/"+artistReseaux
                            .get("facebook")+"/");
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
                }
            });
        }

        if(artistReseaux.get("instagram").equals("")){
            ig.setVisibility(View.GONE);
        }else{
            ig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriUrl = Uri.parse("https://www.instagram.com/"+artistReseaux
                            .get("instagram")+"/");
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
                }
            });
        }

        if(artistReseaux.get("siteWeb").equals("")){
            web.setVisibility(View.GONE);
        }else{
            web.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriUrl = Uri.parse(artistReseaux.get("siteWeb"));
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
                }
            });
        }

        if(artistReseaux.get("twitter").equals("")){
            twit.setVisibility(View.GONE);
        }else{
            twit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriUrl = Uri.parse("https://twitter.com/"+artistReseaux
                            .get("twitter")+"/");
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
                }
            });
        }

        if(artistReseaux.get("youtube").equals("")){
            yt.setVisibility(View.GONE);
        }else{
            yt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uriUrl = Uri.parse("https://www.youtube.com/"+artistReseaux
                            .get("youtube")+"/");
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
                }
            });
        }
    }
}
