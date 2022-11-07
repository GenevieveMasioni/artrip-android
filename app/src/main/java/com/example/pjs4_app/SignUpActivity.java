package com.example.pjs4_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pjs4_app.accesBD.CollectionJoueurs;
import com.example.pjs4_app.artripClasses.MD5;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    private EditText email;
    private EditText pseudo;
    private EditText mdp;
    private EditText confirmationmdp;
    private Button signUP;

    private static ArrayList<String> pseudos ;

    /**
     * Creates the activity, displays it and sets the attributes
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.EmailInput);
        pseudo = findViewById(R.id.PseudoInput);
        mdp = findViewById(R.id.MDPInput);
        confirmationmdp = findViewById(R.id.ConfirmationMDPInput);
        signUP = findViewById(R.id.ConfirmationMDPBtn);

        pseudos = new ArrayList<>();

        /**
         * Gets all the pseudos of the database
         */
        pseudosList(new pseudosCallback() {
            @Override
            public void onCallback(ArrayList value) {

            }
        });

        /**
         * Signs up a user
         */
        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFields()){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword
                            (email.getText().toString().trim()
                            , mdp.getText().toString().trim()).addOnCompleteListener
                            (new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final AuthResult doc = task.getResult();
                                String mdpMD5 = MD5.getMd5(mdp.getText().toString());
                                CollectionJoueurs.createUser(doc.getUser().getUid(),
                                        pseudo.getText().toString(), mdpMD5)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseAuth.getInstance().updateCurrentUser(doc.getUser());
                                        Intent i = new Intent(getApplicationContext(),
                                                LevelMenuActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Utilisateur existant", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

        });

    }

    /**
     * Gets database's users pseudos
     * @param callback
     */
    public void pseudosList(final pseudosCallback callback){
        CollectionJoueurs.getJoueursCollection().get().addOnCompleteListener
                (new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot d = task.getResult();
                List<DocumentSnapshot> allUsers = d.getDocuments();
                for(int i = 0; i < allUsers.size(); i++){
                    pseudos.add(allUsers.get(i).get("pseudo").toString());
                }
                callback.onCallback(pseudos);
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
        }else if(pseudo.length() == 0){
            pseudo.setError("Pseudo erroné");
            isChecked = false;
        }else if(pseudos.contains(pseudo.getText().toString())) {
            pseudo.setError("Pseudo déjà utiliser");
            isChecked = false;
        }else if(!(mdp.length() >= 6)){
            mdp.setError("Mot de passe trop court");
            isChecked = false;
        }else if(!(confirmationmdp.getText().toString().equals(mdp.getText().toString()))){
            confirmationmdp.setError("Différent du mot de passe insérer");
            isChecked = false;
        } else{
            return isChecked;
        }

        return isChecked;
    }

    /**
     * Counters the sync/async problem
     */
    public interface pseudosCallback {
        void onCallback(ArrayList value);
    }

    /**
     * Returns to the previous activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AuthActivity.class));
    }
}
