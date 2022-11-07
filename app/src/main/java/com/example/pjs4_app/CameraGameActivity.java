package com.example.pjs4_app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pjs4_app.accesBD.CollectionJoueurs;
import com.example.pjs4_app.accesBD.CollectionOeuvres;
import com.example.pjs4_app.artripClasses.Artwork;
import com.example.pjs4_app.artripClasses.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

public class CameraGameActivity extends AppCompatActivity {
    private static final int RETOUR_PRENDRE_PHOTO = 1;
    private ImageButton btnphoto;
    private ImageButton btnscn;
    private ImageButton btnImport;
    private Button btnaccueil;
    private LinearLayout mediabuttons;
    private ProgressBar progressBar;
    private File photoFile;

    private ImageView img;
    private Bitmap image;
    private String photoPath = null;
    private String nomOeuvre;


    private Button btnHelp;
    private RelativeLayout helperOverlay;
    private boolean helperOn = false;

    private ArrayList<LatLng> parcours;
    private LatLng positionMarker;

    private boolean bundleMap;
    private String cardID;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int RETOUR_PERMISSION=3;
    private static final int RESULT_LOAD_PHOTO = 2;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Creates, displays and sets the Activity and it's attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        img = findViewById(R.id.imageView);
        btnphoto = findViewById(R.id.cameraButton);
        btnscn = findViewById(R.id.downloadButton);
        btnImport = findViewById(R.id.galleryButton);
        btnaccueil = findViewById(R.id.homeButton);
        mediabuttons = findViewById(R.id.mediaButtons);
        progressBar = findViewById(R.id.progressBar);
        btnHelp = findViewById(R.id.helpButton);
        helperOverlay = findViewById(R.id.helperOverlay);

        parcours = (ArrayList<LatLng>) getIntent().getExtras().get("positions");
        cardID = getIntent().getExtras().getString("cardID");
        positionMarker = (LatLng) getIntent().getExtras().get("positionMarker");

        bundleMap = false;

//      prompt the user to grant permissions
        verifyStoragePermissions(this);

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

        /**
         * Opens the camera and allows the user to take a picture
         */
        btnphoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        /**
         * Calls watson for it to recognized the given image and displays it card
         */
        btnscn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediabuttons.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                //   run a Watson request on a separate thread (security reasons)
                Thread WatsonThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Boolean watsonSuccess;
                        try {
                            watsonSuccess = watsonRequest(photoPath);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    mediabuttons.setVisibility(View.VISIBLE);
                                }
                            });


                            if (watsonSuccess) {
                                isTheGoodArtwork(nomOeuvre.toLowerCase(), positionMarker,
                                        new isGoodArtWorkCallback() {
                                    @Override
                                    public void onCallback(LatLng goodMarker) {
                                        addFoundArtwork(nomOeuvre.toLowerCase(), 50,
                                                positionMarker);
                                        final Intent i = new Intent(
                                                CameraGameActivity.this,
                                                ArtistCardActivity.class);
                                        bundleMap = true;
                                        i.putExtra("bundleMap", bundleMap);
                                        i.putExtra("nomOeuvre", nomOeuvre.toLowerCase());
                                        i.putExtra("goodMarker", goodMarker);
                                        i.putExtra("positions", parcours);
                                        saveImageInUserStorage(nomOeuvre.toLowerCase());
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast t =  Toast.makeText(getApplicationContext(),
                                                "Erreur : oeuvre non reconnue. Veuillez " +
                                                        "ressayer.", Toast.LENGTH_LONG);
                                        TextView v = t.getView().findViewById(android.R.id.message);
                                        v.setTextColor(Color.rgb(0,0,0));
                                        t.getView().setBackgroundColor(Color.rgb(250,
                                                211,46));
                                        t.show();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            final String err = e.toString();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Erreur : " + err, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });

                WatsonThread.start();
            }
        });

        /**
         * Allows the user to use an image of his phone gallery
         */
        btnImport.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_LOAD_PHOTO);
                finish();
            }
        });

        /**
         * Gets back to the previous Activity
         */
        btnaccueil.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTheGoodArtwork(nomOeuvre.toLowerCase(), positionMarker,
                        new isGoodArtWorkCallback() {
                            @Override
                            public void onCallback(LatLng goodMarker) {
                                addFoundArtwork(nomOeuvre.toLowerCase(), 50,
                                        positionMarker);
                                final Intent i = new Intent(
                                        CameraGameActivity.this,
                                        MapsActivity.class);
                                i.putExtra("nomOeuvre", nomOeuvre.toLowerCase());
                                i.putExtra("goodMarker", goodMarker);
                                i.putExtra("positions", parcours);
                                startActivity(i);
                                finish();
                            }
                        });
            }
        });

    }

    /**
     * Allows the user to take a picture from his phone's camera
     */
    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null) {
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File photoDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                photoFile = File.createTempFile("photo" + time, ".jpg", photoDir);
                photoPath = photoFile.getAbsolutePath();
                Uri photoUri = FileProvider.getUriForFile(getApplicationContext(),
                        getApplicationContext().getPackageName() + ".provider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, RETOUR_PRENDRE_PHOTO);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Saves the image in the user storage
     * @param name
     */
    private void saveImageInUserStorage(String name){
        String file_path =  Environment.getExternalStorageDirectory().getAbsolutePath()+
                "/"+getString(R.string.app_name);
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, name+".jpg");
        if(file.exists()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Image existante dans " +
                            "le répertoire Artrip ", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        photoPath = file.toString();
        try {
            FileOutputStream fOut =  new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100,fOut);
            fOut.flush();
            fOut.close();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Image sauvegarder",
                            Toast.LENGTH_SHORT).show();
                }
            });

            MakeSureFileWasCreatedThenMakeAvalible(file);
        } catch (Exception e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Erreur : il n'y a pas d'image.",
                            Toast.LENGTH_LONG).show();
                }
            });
            e.printStackTrace();
        }
    }

    /**
     * Makes sure that the image was created and makes it available to get
     * @param file
     */
    private void MakeSureFileWasCreatedThenMakeAvalible(File file) {
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.toString()},
                null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
            }
        });
    }

    /**
     * Puts the image in the background of this activity so the user can see what he is sending
     * to the IA
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RETOUR_PRENDRE_PHOTO && resultCode == RESULT_OK) {
            image = BitmapFactory.decodeFile(photoPath);
            img.setImageBitmap(image);
        }
        if (requestCode == RESULT_LOAD_PHOTO && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            photoPath = getPhotoPath(getApplicationContext(), uri);
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                image = BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Classifies an image
     * @param path
     * @return Classed image
     */
    public boolean watsonRequest(String path) {
        String myKey = getString(R.string.WatsonApiKey);
        IamOptions options = new IamOptions.Builder()
                .apiKey(myKey)
                .url("https://gateway.watsonplatform.net/visual-recognition/api")
                .build();

        VisualRecognition service = new VisualRecognition("2018-03-19");
        service.setEndPoint("https://gateway.watsonplatform.net/visual-recognition/api");
        service.setUsernameAndPassword("apikey", myKey);

        try {
            File imgFile = new File(path);
            InputStream imagesStream = new FileInputStream(imgFile);
            ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
                    .imagesFile(imagesStream)
                    .imagesFilename(path)
                    .threshold((float) 0.8)
                    .owners(Arrays.asList("me"))
                    .build();
            ClassifiedImages result = service.classify(classifyOptions).execute();
            System.out.println(result);
            nomOeuvre = result.getImages().get(0).getClassifiers().get(0).getClasses().get(0)
                    .getClassName();
            return true;
        } catch (Exception e) {
        }
        return false;
    }


    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission
                .READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * Checks if the position of the artwork that the user found equals the position of the effctive
     * artwork in the database
     * @param artworkName
     * @param positionMarker
     * @param callback
     */
    public void isTheGoodArtwork(final String artworkName, final LatLng positionMarker,
                                 final isGoodArtWorkCallback callback){
        CollectionOeuvres.getOeuvre(artworkName).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot d = task.getResult();
                if(d.exists()){
                    Artwork a = d.toObject(Artwork.class);
                    if(a.position().equals(positionMarker)){
                        callback.onCallback(positionMarker);
                    }else{
                        Toast t =  Toast.makeText(getApplicationContext(),"Ceci n'est pas "+
                                a.getNom()+" . Veuillez réessayer !", Toast.LENGTH_SHORT);
                        TextView v = t.getView().findViewById(android.R.id.message);
                        v.setTextColor(Color.rgb(0,0,0));
                        t.getView().setBackgroundColor(Color.rgb(250,211,46));
                        t.show();
                    }
                }
            }
        });
    }

    /**
     * Add the artwork found by the user to the reserved collection in the database
     * @param nomOeuvre
     * @param pointWon
     * @param positionMarker
     */
    public void addFoundArtwork(String nomOeuvre, final Integer pointWon, LatLng positionMarker){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        GeoPoint p = new GeoPoint(positionMarker.latitude, positionMarker.longitude);
        Artwork o = new Artwork(nomOeuvre, cardID ,p);
        CollectionJoueurs.getJoueursCollection().document(user.getUid()).collection(
                "oeuvres trouvées").document(nomOeuvre).set(o).addOnCompleteListener
                (new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                CollectionJoueurs.getUser(user.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot d = task.getResult();
                        if(d.exists()){
                            User u = d.toObject(User.class);
                            DocumentReference dr = CollectionJoueurs.getJoueursCollection()
                                    .document(user.getUid());
                            dr.update("score", (u.getScore()+pointWon));
                            Toast t =  Toast.makeText(getApplicationContext(),
                                    "Toi, Touriste, avoir gagner 50 points! Toi être sur la " +
                                            "bonne voie", Toast.LENGTH_LONG);
                            TextView v = t.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.rgb(0,0,0));
                            t.getView().setBackgroundColor(Color.rgb(250,211,46));
                            t.show();
                        }
                    }
                });
            }
        });
    }

    /**
     * Gets the absolute path of an Uri objec
     * @param c
     * @param contentUri
     * @return contentUri path
     */
    public String getPhotoPath(Context c, Uri contentUri){
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = c.getContentResolver().query(contentUri, proj,
                    null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }
    }

    /**
     *
     */
    public interface isGoodArtWorkCallback {
        void onCallback(LatLng goodMarker);
    }

    /**
     * Gets back to the previous activity
     */
    @Override
    public void onBackPressed() {
       if(!(nomOeuvre == null)) {
            isTheGoodArtwork(nomOeuvre.toLowerCase(), positionMarker,
                    new isGoodArtWorkCallback() {
                        @Override
                        public void onCallback(LatLng goodMarker) {
                            addFoundArtwork(nomOeuvre.toLowerCase(), 50,
                                    positionMarker);
                            final Intent i = new Intent(
                                    CameraGameActivity.this,
                                    MapsActivity.class);
                            i.putExtra("nomOeuvre", nomOeuvre.toLowerCase());
                            i.putExtra("goodMarker", goodMarker);
                            i.putExtra("positions", parcours);
                            startActivity(i);
                            finish();
                        }
                    });
        }else{
            final Intent i = new Intent(
                    CameraGameActivity.this,
                    MapsActivity.class);
            i.putExtra("positions", parcours);
            startActivity(i);
            finish();
        }
    }
}
