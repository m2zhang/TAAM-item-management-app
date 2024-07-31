package com.example.b07demosummer2024;

import android.os.Bundle;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ProgressBar;
import android.widget.Button;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;  // Add this import
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnItemSelectedListener {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private ProgressBar progressBar;
    private Item selectedItem;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        Button buttonRemoveItem = view.findViewById(R.id.buttonRemove);
        Button buttonHome = view.findViewById(R.id.buttonHome);
        Button buttonSearch = view.findViewById(R.id.buttonSearch);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        Button buttonReport = view.findViewById(R.id.buttonReport);


        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Initialize and set the adapter for RecyclerView


        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList, this);

        recyclerView.setAdapter(itemAdapter);

        progressBar = view.findViewById(R.id.progress_bar);

        db = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/");

        fetchItemsFromDatabase();

        // Search button
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new AddActivityFragment());
            }
        });

        buttonRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = itemAdapter.getSelectedPosition();
                if (selectedPosition != -1) {
                    Item selectedItem = itemAdapter.getItem(selectedPosition);
                    int lotNumber = selectedItem.getLotNumber();
                    String name = selectedItem.getName();
                    String category = selectedItem.getCategory();
                    String period = selectedItem.getPeriod();
                    String description = selectedItem.getDescription();
                    String picture = selectedItem.getPicture();
                    String video = selectedItem.getVideo();

                    RemoveItemFragment removeItemFragment = RemoveItemFragment.newInstance(lotNumber, name, category, period, description, picture, video);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, removeItemFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        // Home button functionality
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savedInstanceState == null) {
                    loadFragment(new HomeFragment());
                }
            }
        });

        Button buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start AnotherActivity
                Intent intent = new Intent(getActivity(), GeneralUserActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(Item item) {
        selectedItem = item;
    }

    private void fetchItemsFromDatabase() {
        itemsRef = db.getReference("Items");

        progressBar.setVisibility(View.VISIBLE);

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item item = safeDataSnapshotToItem(snapshot);
                    if (item != null) {
                        itemList.add(item);
                    }
                }
                itemAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private Item safeDataSnapshotToItem(DataSnapshot dataSnapshot) {
        try {
            Item item = dataSnapshot.getValue(Item.class);

            if (item != null) {
                Object lotNumberObj = dataSnapshot.child("lotNumber").getValue();
                if (lotNumberObj instanceof String) {
                    item.setLotNumber(Integer.parseInt((String) lotNumberObj));
                } else if (lotNumberObj instanceof Long) {
                    item.setLotNumber(((Long) lotNumberObj).intValue());
                }
            }

            return item;
        } catch (DatabaseException | NumberFormatException e) {
            Log.e("HomeFragment", "Data conversion error: " + e.getMessage());
            return null;
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
