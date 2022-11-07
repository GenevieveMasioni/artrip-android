package com.example.pjs4_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {
    private boolean helperOn = false;
    private RelativeLayout helperOverlay;
    private Button btnHelp;

    /**
     * Creates the activity, displays it and sets the attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button btnBalade = findViewById(R.id.buttonBalade);
        Button btnExplorateur = findViewById(R.id.buttonExplorateur);
        Button btnSieste = findViewById(R.id.buttonSieste);
        btnHelp = findViewById(R.id.helpButton);
        helperOverlay = findViewById(R.id.helperOverlay);

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
         * Starts the Camera activity
         */
        btnBalade.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(i);
                finish();
            }
        });

        /**
         * Starts the Auth Activity
         */
        btnExplorateur.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AuthActivity.class);
                startActivity(i);
                finish();
            }
        });

        /**
         * Starts the Sleep Activity
         */
        btnSieste.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SleepActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * Gets back to this activity
     */
    @Override
    public void onBackPressed() {
        if(helperOn) {
            btnHelp.setBackground(getDrawable(R.drawable.help_button_dark));
            helperOverlay.setVisibility(View.INVISIBLE);
            helperOn = false;
        }else{
        this.finishAffinity();}
    }
}
