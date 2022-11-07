package com.example.pjs4_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.pjs4_app.accesBD.CollectionJoueurs;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArchivesFragment extends Fragment {
    private ArrayList<String> myCollection;
    private List<StorageReference> allItems;
    private ArrayList<LatLng> b;
    private GridView gridview;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();


    /**
     * Required empty public constructor
     */
    public ArchivesFragment() {

    }

    /**
     * Creates the fragment
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Defines the inflater where the fragment will be displayed
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return inflater
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_archives, container, false);
    }

    /**
     * Displays the fragment
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gridview =  getView().findViewById(R.id.gridviewTest);
        allItems = new ArrayList<>();
        myCollection = new ArrayList<>();

        getUnlockedArtworksName(new UnlockedArtworksTitleCallback() {
            @Override
            public void onCallback(ArrayList titles) {
                myCollection = titles;
                for(String n : myCollection){
                    allItems.add(storageReference.child("imagesPrincipalesOeuvres/" + n + ".jpg"));
                }
                final CustomAdapter customAdapter = new CustomAdapter(getContext(), allItems);
                gridview.setAdapter(customAdapter);

                /**
                 * On image clicked displays the associated card
                 */
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getContext(), ArtistCardActivity.class);
                        Bundle bd = getActivity().getIntent().getExtras();
                        if (bd != null) {
                            b = (ArrayList<LatLng>) bd.get("positions");
                        }
                        i.putExtra("nomOeuvre", myCollection.get(position));
                        i.putExtra("positions", b);
                        i.putExtra("bundleArchivesFragment", true);
                        startActivity(i);
                    }
                });
            }
        });
    }

    /**
     * Gets the unlocked artworks name
     * @param callback
     */
    private void getUnlockedArtworksName(final UnlockedArtworksTitleCallback callback){
        CollectionJoueurs.getJoueursCollection().document(FirebaseAuth.getInstance()
                .getCurrentUser().getUid())
                .collection("oeuvres trouvées").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot d = task.getResult();
                            List<DocumentSnapshot> allUnlockedArtworks = d.getDocuments();
                            for (int i = 0; i < allUnlockedArtworks.size(); i++){
                                myCollection.add(allUnlockedArtworks.get(i).getId());
                            }
                            callback.onCallback(myCollection);
                        }
                    }
                });
    }

    /**
     * Counters the async/sync problem by returning the filled arrayList
     */
    private interface UnlockedArtworksTitleCallback {
        void onCallback(ArrayList titles);
    }

}
