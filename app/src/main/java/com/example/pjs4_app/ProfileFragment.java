package com.example.pjs4_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pjs4_app.accesBD.CollectionJoueurs;
import com.example.pjs4_app.artripClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView userInfos;
    private TextView numOfUnlockedArtworks;
    private TextView score;

    /**
     * Required empty public constructor
     */
    public ProfileFragment() {
    }

    /**
     * Creates the fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfos = getActivity().findViewById(R.id.userInformation);
    }

    /**
     * Defines the inflater where the fragment will be displayed
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return inflater
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    /**
     * Displays the fragment
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userInfos = getView().findViewById(R.id.userInformation);
        numOfUnlockedArtworks = getView().findViewById(R.id.numOfUnlockedArtworks);
        score = getView().findViewById(R.id.score);

        getUserInfos(new UserInfosCallback() {
            @Override
            public void onCallback(String emailAdress, Integer unlockedArtworksNum,Integer points) {
                userInfos.setText(emailAdress);
                score.setText(points.toString());
                numOfUnlockedArtworks.setText(unlockedArtworksNum.toString());
            }
        });
    }

    /**
     * Gets the user information from the database
     * @param callback
     */
    private void getUserInfos(final UserInfosCallback callback){
        CollectionJoueurs.getJoueursCollection()
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot d = task.getResult();
                    final User u = d.toObject(User.class);
                    CollectionJoueurs.getJoueursCollection().document(
                            FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .collection("oeuvres trouvées").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot d = task.getResult();
                                callback.onCallback("E-mail : "+
                                        FirebaseAuth.getInstance().getCurrentUser()
                                        .getEmail(), d.size(), u.getScore());
                            }
                        }
                    });
                }else{
                    Toast.makeText(getContext(), "User doesn't exist", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    /**
     * Counters the sync / async problem by returning the needed information on the user
     */
    private interface UserInfosCallback {
        void onCallback(String emailAdress, Integer unlockedArtworksNum,Integer points);
    }

}
