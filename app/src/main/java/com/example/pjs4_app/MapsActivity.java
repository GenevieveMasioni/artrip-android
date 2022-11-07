package com.example.pjs4_app;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.pjs4_app.accesBD.CollectionJoueurs;
import com.example.pjs4_app.artripClasses.Artwork;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String TAG = "MapsActivity";

    //permissions + valeur par defauts
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;

    //vars
    private Boolean mLocationPermissionsGranted = false;
    public GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient; //avoir derniere localisation
    private GeoApiContext mGeoApiContext = null; //avoir le calcul d'itineraire
    private ArrayList<LatLng> positions;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private LatLng positionMarker;

    private Button homeButton;
    private Button plusButton;
    private Button userButton;

    private Marker UserLocation;

    private Dialog alert;

    private LatLng goodMarker;
    private ArrayList<LatLng> foundArtwork;

    private static final float HUE_GREEN_PERSO = 102;

    String cardID;

    private boolean helperOn = false;
    private RelativeLayout helperOverlay;
    private Button btnHelp;


    /**
     * Creates the activity, displays it and sets the attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        btnHelp = findViewById(R.id.helpButton);
        helperOverlay = findViewById(R.id.helperOverlay);
        alert = new Dialog(MapsActivity.this);

        /**
         * Display the Help Activity
         */
        btnHelp.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!helperOn) {
                    btnHelp.setBackground(getDrawable(R.drawable.help_button_light));
                    helperOverlay.setVisibility(View.VISIBLE);
                    helperOn = true;
                } else {
                    btnHelp.setBackground(getDrawable(R.drawable.help_button_dark));
                    helperOverlay.setVisibility(View.INVISIBLE);
                    helperOn = false;
                }
            }
        });

        //Calls the function that allows us to know if the user allows the geolocalisation
        getLocationPermission();

        positions = (ArrayList<LatLng>) getIntent().getExtras().get("positions");
        cardID = getIntent().getExtras().getString("cardID");
        goodMarker = (LatLng) getIntent().getExtras().get("goodMarker");
        foundArtwork = new ArrayList<>();

        subMenu();
    }

    /**
     * Displays the map when the initialization is done
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Votre carte est prête", Toast.LENGTH_SHORT).show();
        mMap = googleMap;

        /*
        --------------------LOCALISATION--------------------
        */

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (mLocationPermissionsGranted) {
            getLastKnownLocation();

            //Puts a blue marker for the user's localisation
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

            mMap.getUiSettings().setMapToolbarEnabled(false);

            //adds markers on the map
            addMarkers();
        }
    }

    /**
     * Initializes the map
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_API_key))
                    .build();
        }
    }

    /**
     * Gets the user's last Location
     */
    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (mLocationPermissionsGranted) {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener
                    (new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            try {
                                if (task.isSuccessful()) {
                                    Location location = task.getResult();
                                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(),
                                            location.getLongitude());
                                }
                            } catch(Exception e) {
                                Toast.makeText(MapsActivity.this,
                                        R.string.getLastKnownLocationExceptionMessage, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
        }
    }

    /**
     * Ask to the user if he wants to activate the geolocalisation
     */
    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionsGranted = true;
            Log.d(TAG, "getLocationPermission: on rentre dans le granted");
            initMap();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            Toast.makeText(this,
                    "Veuillez accepter puis réappuyer sur le bouton découverte",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Gets the user current location
     * @param context
     * @return user's location
     */
    public static Location getLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService
                (Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getAllProviders();
        Location bestLocation = null;

        for (String provider : providers) {
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                if (bestLocation == null || location != null
                        && location.getElapsedRealtimeNanos() > bestLocation
                        .getElapsedRealtimeNanos()
                        && location.getAccuracy() > bestLocation.getAccuracy())
                    bestLocation = location;
            } catch (SecurityException ignored) {
            }
        }

        return bestLocation;
    }


    /**
     * Zooms on the map from the user location to the artworks ones
     * @param lstLatLong
     */
    public void zoomRoute(List<LatLng> lstLatLong) {
        if (mMap == null || lstLatLong == null || lstLatLong.isEmpty() ) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng a: lstLatLong)
            boundsBuilder.include(a);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }


    /*
    ---------------------------PARTIE_OEUVRE-------------------------------
     */

    /**
     * Add the artworks position with a marker for each one
     * Checks if the user is close to one and enables the cameraGame activity
     * If not diplays an cheat tentative alert
     */
    public void addMarkers(){
        getFoundArtworkList(new foundArtworkCallback() {
            @Override
            public void onCallback(final ArrayList value) {
                for (LatLng l: positions) {
                    if(foundArtwork.contains(l)){
                        markers.add(mMap.addMarker(new MarkerOptions()
                                .position(l)
                                .icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN_PERSO)))
                        );
                    }else if(l.equals(goodMarker)){
                        markers.add(mMap.addMarker(new MarkerOptions()
                                .position(l)
                                .icon(BitmapDescriptorFactory.defaultMarker(HUE_GREEN_PERSO)))
                        );
                    }else{
                        markers.add(mMap.addMarker(new MarkerOptions().position(l)));
                    }
                }

                LatLng UserLatLng = new LatLng(getLocation(getApplicationContext()).getLatitude()
                        , getLocation(getApplicationContext()).getLongitude());
                UserLocation = mMap.addMarker(new MarkerOptions()
                        .position(UserLatLng)
                        .title(getString(R.string.addMarkersUserLocationTitle))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory
                                .HUE_AZURE))
                );
                UserLocation.showInfoWindow();
                markers.add(UserLocation);


                //Verifies if the user is close to a marker to make it clickable
                //otherwise an Alert is shown and the user doesn't get any points
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker.equals(UserLocation)) {
                            Log.d(TAG, "onMarkerClick: Click on userLocation");
                            return false;
                        }
                        if(foundArtwork.contains(marker.getPosition())){
                            alert.setContentView(R.layout.dialog_oeuvre_debloquee_view);
                            alert.show();
                            final Handler handler = new Handler();
                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                }
                            };
                            handler.postDelayed(runnable, 3000);
                        }else if (!foundArtwork.contains(marker.getPosition()) &&
                                getDistanceBetween(UserLocation.getPosition(), marker.getPosition()
                        ) <= 500) {
                            Intent intent = new Intent(MapsActivity.this,
                                    CameraGameActivity.class);
                            positionMarker = marker.getPosition();
                            intent.putExtra("positions", positions);
                            intent.putExtra("positionMarker", positionMarker);
                            intent.putExtra("cardID", cardID);
                            startActivity(intent);
                        } else {
                            alert.setContentView(R.layout.dialog_triche_view);
                            alert.show();
                            final Handler handler = new Handler();
                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                }
                            };
                            handler.postDelayed(runnable, 3000);
                        }
                        return true;
                    }
                });

                //Generates the journey route
                for (int i = 1; i < markers.size(); i++) {
                    calculateDirections(markers.get(i - 1), markers.get(i));
                }
            }
        });
    }

    /**
     * Calculates the routes with the DIrection API
     * @param markerOrigin
     * @param markerDestination
     */
    private void calculateDirections(Marker markerOrigin, Marker markerDestination){

        final com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                markerDestination.getPosition().latitude,
                markerDestination.getPosition().longitude
        );

        final DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(false);
        directions.mode(TravelMode.WALKING);

        directions.origin(
                new com.google.maps.model.LatLng(
                        markerOrigin.getPosition().latitude,
                        markerOrigin.getPosition().longitude
                )
        );

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                //Creates the drawn routes
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
            }
        });
    }

    /**
     * Adds and draws the route to the map
     * @param result
     */
    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                for(DirectionsRoute route: result.routes){
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    polyline.setWidth(10);
                    //Calls the camera on the routes
                    zoomRoute(polyline.getPoints());
                }
            }
        });
    }

    /*
    ---------------------------PARTIE_BOUTON-------------------------------
    */

    /**
     * Changes the visibility of the sub menu activity according to if the button + click
     */
    private void changeVisibility() {
        LinearLayout buttonLayout = findViewById(R.id.mapsLayoutButton);
        if (buttonLayout.getVisibility() == View.INVISIBLE) {
            buttonLayout.setVisibility(View.VISIBLE);
            plusButton.setBackground(getDrawable(R.drawable.minus_button));
            //Animation fadeIn
            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
            buttonLayout.startAnimation(animFadeIn);
        }
        else {
            buttonLayout.setVisibility(View.INVISIBLE);
            plusButton.setBackground(getDrawable(R.drawable.plus_button));
            //Animation fadeOut
            Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
            buttonLayout.startAnimation(animFadeOut);
        }
    }

    /**
     * Creates the sub menu in the map activity
     */
    private void subMenu() {
        plusButton =  findViewById(R.id.mapsPlusButton);
        homeButton =  findViewById(R.id.mapsHomeButton);
        userButton =  findViewById(R.id.userProfileButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeVisibility();
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LevelMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("positions", positions);
                startActivity(intent);
                finish();
            }
        });
    }


    /**
     * Gets database's users found artworks
     * @param callback
     */
    private void getFoundArtworkList(final foundArtworkCallback callback){
        CollectionJoueurs.getJoueursCollection().document(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).collection("oeuvres trouvées")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot d = task.getResult();
                final List<DocumentSnapshot> allArtworks = d.getDocuments();
                for(int i = 0; i < allArtworks.size(); i++){
                    Artwork o = allArtworks.get(i).toObject(Artwork.class);
                    foundArtwork.add(o.position());
                }
                callback.onCallback(foundArtwork);
            }
        });

    }

    /**
     * Calculates the distance between to points
     * @param source
     * @param dest
     * @return
     */
    private float getDistanceBetween(LatLng source, LatLng dest){
        return distFrom((float)source.latitude, (float)source.longitude,
                (float)dest.latitude, (float)dest.longitude);
    }

    /**
     * Calculates the distance between to points
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    private int distFrom (float lat1, float lng1, float lat2, float lng2 ){
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        return (int) dist*1000;
    }


    /**
     * Gets back to the previous activity
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MapsActivity.this, FilterMenuActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Counters async/sync problem by returning the list of foundArtwork
     */
    public interface foundArtworkCallback {
        void onCallback(ArrayList value);
    }
}
