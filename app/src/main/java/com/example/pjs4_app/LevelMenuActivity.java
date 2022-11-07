package com.example.pjs4_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LevelMenuActivity extends AppCompatActivity {
    private Button btnTouriste;
    private Button btnArtiste;
    private Button btnMécène;
    private Button btnBackToMenu;

    /**
     * Creates the activity, displays it and sets the attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_menu);

        btnTouriste =  findViewById(R.id.buttonTouriste);
        btnArtiste =  findViewById(R.id.buttonExplo);
        btnMécène = findViewById(R.id.buttonArtiste);
        btnBackToMenu = findViewById(R.id.buttonBackToMenu);

        /**
         * Gets back to the previous activity and disconnect the current user
         */
        btnBackToMenu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AuthActivity.class);
                Toast.makeText(getApplicationContext(),
                        "Vous vous êtes bien déconnecté !", Toast.LENGTH_SHORT).show();
                startActivity(i);
                finish();
            }
        });

        final Intent i = new Intent(getApplicationContext(), FilterMenuActivity.class);

        /**
         * All the following buttons lead to the Filter menu activity
         */
        btnTouriste.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
                finish();
            }
        });

        btnArtiste.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
                finish();
            }
        });

        btnMécène.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Gets back to the previous activity and disconnect the current user
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AuthActivity.class));
        Toast.makeText(getApplicationContext(),
                "Vous vous êtes bien déconnecté.e !", Toast.LENGTH_SHORT).show();
        finish();
    }
}
