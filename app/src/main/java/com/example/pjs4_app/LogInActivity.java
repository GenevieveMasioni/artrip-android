package com.example.pjs4_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class LogInActivity extends AppCompatActivity {
    private EditText email;
    private EditText mdp;
    private Button signIn;

    /**
     * Creates the activity, displays it and sets the attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        email = findViewById(R.id.EmailInput);
        mdp = findViewById(R.id.MDPInput);
        signIn = findViewById(R.id.GoBtn);

        /**
         * Logs the user in the game part of the app
         */
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFields()){
                    FirebaseAuth.getInstance().signInWithEmailAndPassword
                            (email.getText().toString().trim(), mdp.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                AuthResult res = task.getResult();
                                FirebaseUser user = res.getUser();
                                FirebaseAuth.getInstance().updateCurrentUser(user);
                                Intent i = new Intent(getApplicationContext(),
                                        LevelMenuActivity.class);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),
                                        "Utilisateur non inscrit ou mdp erroné",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


    }

    /**
     * Checks if all fields are valid before logging in
     * @return true or false according to the validity of the fields
     */
    public boolean checkFields(){
        boolean isChecked = true;
        if(email.length() == 0){
            email.setError("Email erroné");
            isChecked=false;
        }else if(!(Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().toLowerCase().trim()).matches())){
            email.setError("Email erroné");
            isChecked = false;
        }else if(!(mdp.length() >= 6)){
            mdp.setError("Mot de passe trop court");
            isChecked = false;
        } else{
            return isChecked;
        }

        return isChecked;
    }

    /**
     * Gets to the previous activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AuthActivity.class));
    }

}
