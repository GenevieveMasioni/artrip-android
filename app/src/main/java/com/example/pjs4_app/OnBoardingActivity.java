package com.example.pjs4_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class OnBoardingActivity extends AppCompatActivity {

    private ViewPager screenPager;
    private OnBoardingViewerPagerAdapter adapter;
    private TabLayout tabIndicator;
    private Button btnSkip;
    private Button btnCommencer;
    private Animation btnAnimation;
    private int position = 0;

    /**
     * Creates the activity, displays it and sets the attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (restorePrefData()) {
//            Intent mainActivity = new Intent(getApplicationContext(),MainMenuActivity.class );
//            startActivity(mainActivity);
//            finish();
//        }
        setContentView(R.layout.activity_on_boarding);

        /**
         * Initializes all the slide of the boarding Activity
         */
        final List<ScreenItem> items = new ArrayList<>();
        items.add(new ScreenItem("ARTRIP ?  L\'ART URBAIN DANS LA POCHE",
                "Découvre Paris en mode street art, de manière interactive et ludique !",
                getDrawable(R.drawable.bg_gradation)));
        items.add(new ScreenItem("TU ADORES UNE OEUVRE URBAINE MAIS TU NE CONNAIS PAS " +
                "L\'ARTISTE ?", "Peut-être qu'on le sait ! \n\n1. Prends-la en photo ou " +
                "importe-la de ta galerie \n\n 2. Envoie-la à notre IA pour analyse",
                getDrawable(R.drawable.scan)));
        items.add(new ScreenItem("\n\n\n\n\n\nENFIN TROUVÉE !!!",
                "3. L\'IA te retourne la fiche d\'informations sur l\'oeuvre scannée",
                getDrawable(R.drawable.fiche)));
        items.add(new ScreenItem("\n\n\n\nESPRIT AVENTURIER OU COLLECTIONNEUR ? ",
                "Connecte-toi et vois combien d\'oeuvres tu peux collectionner à " +
                        "travers la ville !",getDrawable(R.drawable.login)));
        items.add(new ScreenItem("\n\n\n\nPLUTÔT DEBUTANT.E OU CONFIRMÉ.E ?",
                "Choisis le niveau qui te convient, du plus facile au plus difficile. " +
                        "Il y a des oeuvres pour tous !", getDrawable(R.drawable.niveau)));
        items.add(new ScreenItem("\n\n\n\nQUE VAIS-JE TROUVER AUJOURD\'HUI ?", "Un.e artiste " +
                "en tête ? Parcours par artiste ! Le goût de l\'aventure ? " +
                "Le parcours Découverte est pour toi !",getDrawable(R.drawable.filtre)));
        items.add(new ScreenItem("\n\n\n\nTU ES DEVANT UNE OEUVRE ? ", "Clique sur le marqueur et " +
                "scan l\'oeuvre. Bonne réponse ? Tu gagnes des points et débloques l\'oeuvre !",
                getDrawable(R.drawable.mode)));

        screenPager = findViewById(R.id.screenPager);
        adapter = new OnBoardingViewerPagerAdapter(this, items);
        screenPager.setAdapter(adapter);
        tabIndicator = findViewById(R.id.screenTab);
        btnSkip = findViewById(R.id.nextButton);
        btnCommencer = findViewById(R.id.startButton);
        btnAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.button_animation);

        tabIndicator.setupWithViewPager(screenPager);

        /**
         * Slides to the next slide (pun unintended)
         */
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(items.size());
                LoadLastScreen();
            }
        });


        /**
         * Starts the main app
         */
        btnCommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
//                savePrefsData();
                finish();
            }
        });

        /**
         * Handles the scrolled slides
         */
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == items.size()-1){
                    LoadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * Allows the app to check if this Activity has already been called
     * In order to not runs it every time the user uses the app (pun unintended)
     * @return true or false
     */
    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;
    }

    /**
     * Saves a boolean tha says if this Activity is called
     */
    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.commit();


    }

    /**
     * Loads the last slide
     */
    private void LoadLastScreen(){
        btnSkip.setVisibility(View.GONE);
        btnCommencer.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);

        btnCommencer.setAnimation(btnAnimation);
    }
}
