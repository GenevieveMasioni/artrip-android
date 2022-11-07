package com.example.pjs4_app;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class SleepActivity extends AppCompatActivity {
    private String file_path =  Environment.getExternalStorageDirectory().getAbsolutePath()
            +"/Artrip";
    private TextView t;
    private List<StorageReference> items;
    private ArrayList<String> maCollection; // list of unlocked artwork titles for grid view
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();
    private File[] files;
    private Button btnclose;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Creates the activity, displays it and sets the attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        verifyStoragePermissions(this);

        File repertoire = new File(file_path);
        items = new ArrayList<>();
        maCollection = new ArrayList<>();
        files = repertoire.listFiles();
        GridView gridview = findViewById(R.id.gridview);
        if (files.length == 0)
            t.setText("Aucune Photo");

        btnclose = findViewById(R.id.closeGalerieButton);
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
            }
        });

        /**
         * Gets all the images of the ArTrip directory
         */
        for (int i = 0; i < files.length; i++) {
            if (files[i].length() != 0) {
                String[] artworkName = files[i].getName().split("\\.");
                String a = artworkName[0];
                maCollection.add(a);
                items.add(storageReference.child("imagesPrincipalesOeuvres/" + a + ".jpg"));
            }
        }

        CustomAdapter customAdapter = new CustomAdapter(SleepActivity.this, items);
        gridview.setAdapter(customAdapter);

        /**
         * On image clicked displays the associated card
         */
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArtistCardActivity.class);
                i.putExtra("nomOeuvre", maCollection.get(position));
                i.putExtra("bundleSieste", true);
                startActivity(i);
            }
        });
    }

    /**
     * Returns to the previous activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
        finish();
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
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
