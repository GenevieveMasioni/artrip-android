package com.example.pjs4_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pjs4_app.accesBD.CollectionJoueurs;
import com.example.pjs4_app.artripClasses.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

public class UserActivity extends AppCompatActivity {
    private TextView username;

    private TabLayout tabsWrapper;
    private ViewPager viewPager;
    private PageAdapter pageAdapter;
    private ArrayList<LatLng> pos;
    private Button closeProfile;

    /**
     * Creates the activity, displays it and sets the attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        username = findViewById(R.id.username);
        tabsWrapper = findViewById(R.id.tabsWrapper);
        viewPager = findViewById(R.id.contentFrame);
        closeProfile = findViewById(R.id.closeProfileButton);

        TabLayout.Tab profileTab = tabsWrapper.newTab();
        TabLayout.Tab archivesTab = tabsWrapper.newTab();
        profileTab.setText(R.string.profileTabTitle);
        archivesTab.setText(R.string.archivesTabTitle);
        tabsWrapper.addTab(profileTab,0);
        tabsWrapper.addTab(archivesTab,1);

        LinearLayout tabStrip = ((LinearLayout)tabsWrapper.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabsWrapper.getTabCount());
        viewPager.setAdapter(pageAdapter);
        pos = (ArrayList<LatLng>)getIntent().getExtras().get("positions");

        /**
         * Gets back to the previous activity
         */
        closeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putExtra("positions", pos);
                startActivity(i);
                finish();
            }
        });


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabsWrapper));
        updateFragment(pageAdapter.getItem(viewPager.getCurrentItem()));
        viewPager.setCurrentItem(1);
        getUsername(new usernameCallback() {
            @Override
            public void onCallback(String name) {
                username.setText(name.toUpperCase());
            }
        });
    }

    /**
     * Tab interaction handler to update onscreen content
     *
     * @param fragment the fragement to display
     */
    private void updateFragment(Fragment fragment) {
        Bundle bundle = getIntent().getExtras();
        fragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.contentFrame, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    /**
     * Gets the user pseudo
     * @param callback
     */
    private void getUsername(final usernameCallback callback){
        CollectionJoueurs.getJoueursCollection().document(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot d = task.getResult();
                    User u = d.toObject(User.class);
                    callback.onCallback(u.getPseudo());
                }else{
                    Toast.makeText(getApplicationContext(), "User doesn't exist",
                            Toast.LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * Counters the sync/asyn problem by returning the user pseudo
     */
    public interface usernameCallback {
        void onCallback(String name);
    }
}
