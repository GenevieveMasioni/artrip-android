package com.example.pjs4_app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.pjs4_app.accesBD.CollectionArtiste;
import com.example.pjs4_app.accesBD.CollectionFiches;
import com.example.pjs4_app.accesBD.CollectionOeuvres;
import com.example.pjs4_app.artripClasses.Artwork;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class FilterMenuActivity extends AppCompatActivity {

    private final static String TAG = "FiltreMenuActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;

    private Button btnAleatoire;
    private Button btnArtiste;
    private Button btnBackToLevel;

    private static  ArrayList<String> titres ;
    private ArrayList<LatLng> parcours;
    private static  ArrayList<String> items ;

    private static String cardID;

    /**
     * Creates the activity, displays it and sets the attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_menu);

        btnAleatoire = findViewById(R.id.buttonDecouverte);
        btnArtiste = findViewById(R.id.buttonParArtiste);
        btnBackToLevel = findViewById(R.id.buttonBackToLevel);

        parcours = new ArrayList<>();
        titres = new ArrayList<>();
        items = new ArrayList<>();

        /**
         * Returns to the previous Activity
         */
        btnBackToLevel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LevelMenuActivity.class);
                startActivity(i);
                finish();
            }
        });

        /**
         * Starts the Maps activity with a random journey
         */
        btnAleatoire.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send to the Maps activity as a list through extras
                genererCheminAleatoire(new ParcoursCallback() {
                    @Override
                    public void onCallback(ArrayList value, String cardId) {
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.putExtra("positions", parcours);
                        intent.putExtra("cardID", cardId);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

        /**
         * Starts the map activity with a generated journey base on the selected artist name
         */
        btnArtiste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(new PopupMenuCallback() {
                    @Override
                    public void onCallback(PopupMenu value) {
                        value.show();
                        value.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                getArtworksByArtist(item.toString(), new ParcoursCallback() {
                                    @Override
                                    public void onCallback(ArrayList value, String cardID) {
                                        Intent intent = new Intent(getApplicationContext(),
                                                MapsActivity.class);
                                        intent.putExtra("positions", parcours);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                return true;
                            }
                        });
                    }
                }, v);
            }
        });

        //If GooglePlay is ready then Access to the Map
        if (!isServicesOK()) {
            Toast.makeText(this, R.string.isServicesOKWarningMessage,
                    Toast.LENGTH_SHORT).show();
        }

        //Verifies if the GPS is activated else error message
        final LocationManager manager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE );
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
            buildAlertMessageNoGps();
        }
    }

    /**
     * Asks to activate the user's localisation
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.WarningMessageGPS)
                .setCancelable(false)
                .setNegativeButton((getString(R.string.WarningMessageGPSno)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.setCancelable(true);
                    }
                })
                .setPositiveButton(R.string.WarningMessageGSPyes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Generates a random selection of art from database
     * @param callback
     */
    public void genererCheminAleatoire(final ParcoursCallback callback) {
        getArtworksNames(new artWorksTitleCallback() {
            @Override
            public void onCallback(ArrayList value) {
                final int nbOeuvre = (int) Math.floor(Math.random() * 3 + 2);
                int start = (int) Math.floor(Math.random()*titres.size());
                start = start + nbOeuvre >= titres.size() - 1 ? start - nbOeuvre : start;
                try {
                    for (int i = start; i < start + nbOeuvre; i++) {
                        CollectionOeuvres.getOeuvre(titres.get(i)).addOnCompleteListener
                                (new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        Artwork o = doc.toObject(Artwork.class);
                                        cardID = o.getFiche();
                                        parcours.add(o.position());
                                        callback.onCallback(parcours, cardID);
                                    } else {
                                        Toast.makeText(getApplicationContext()
                                                , "Oeuvres non existante", Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            }
                        });
                    }
                } catch(Exception e) {
                }
            }
        });
    }

    /**
     * Checks if your maps version is not too old
     * @return
     */
    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable
                (FilterMenuActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            //Eveything is checked, the user can continue using the app
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(FilterMenuActivity.this, available,
                            ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "Vous ne pouvez pas avoir accès à la carte",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Gets database's artworks name
     * @param callback
     */
    public void getArtworksNames(final artWorksTitleCallback callback){
        CollectionOeuvres.getOeuvresCollection().get().addOnCompleteListener
                (new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot d = task.getResult();
                List<DocumentSnapshot> allArtworks = d.getDocuments();
                for (int i = 0; i < allArtworks.size(); i++) {
                    titres.add(allArtworks.get(i).getId());

                }
                callback.onCallback(titres);
            }
        });
    }

    /**
     * Gets database's artworks name by Artist
     * @param artistAlias
     * @param callback
     */
    public void getArtworksByArtist(final String artistAlias, final ParcoursCallback callback){
        CollectionOeuvres.getOeuvresCollection().get().addOnCompleteListener
                (new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot d = task.getResult();
                final List<DocumentSnapshot> allArtworks = d.getDocuments();
                for(int i = 0; i< allArtworks.size(); i++){
                    final int j = i;
                    CollectionFiches.getFiche(allArtworks.get(i).get("fiche").toString())
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot doc = task.getResult();
                            if(doc.get("artisteAlias").equals(artistAlias)){
                                parcours.add(allArtworks.get(j).toObject(Artwork.class).position());
                            }
                            callback.onCallback(parcours, cardID);
                        }
                    });
                }
            }
        });
    }

    /**
     * Adds the artist name in the popUpMenu
     * @param callback
     * @param v
     */
    public void addItem(final PopupMenuCallback callback, View v){
        Context wrapper = new ContextThemeWrapper(this, R.style.popUpMenu);
        final PopupMenu test = new PopupMenu(wrapper, v );
        //Not an error keep going
        test.setGravity(Gravity.NO_GRAVITY);
        CollectionArtiste.getArtistesCollection().get().addOnCompleteListener
                (new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot d = task.getResult();
                final List<DocumentSnapshot> allArtists = d.getDocuments();
                for(int i = 0; i < allArtists.size(); i++){
                    test.getMenu().add(allArtists.get(i).getId());
                }
                callback.onCallback(test);
            }
        });
    }

    /**
     * Counters async/sync problem by returning the artwork list
     */
    public interface artWorksTitleCallback {
        void onCallback(ArrayList value);
    }

    /**
     * Counters async/sync problem by returning the list of  the artworks positions
     */
    public interface ParcoursCallback {
        void onCallback(ArrayList value, String cardId);
    }

    /**
     * Counters async/sync problem by setting the Popup menu value
     */
    public interface PopupMenuCallback {
        void onCallback(PopupMenu value);
    }

    /**
     * Returns to the previous activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), LevelMenuActivity.class));
    }
}
