package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnItemSelectedListener {

    private EditText editTextLotNumber, editTextName, editTextCategory, editTextPeriod;
    private TextView textViewResults;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;
    private DatabaseReference itemsRef;
    private Button buttonView;
    private Button buttonBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        Button buttonSearch;
        RecyclerView recyclerViewResults;
        FirebaseDatabase db;

        // Initialize UI elements
        editTextLotNumber = findViewById(R.id.editTextLotNumber);
        editTextName = findViewById(R.id.editTextName);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextPeriod = findViewById(R.id.editTextPeriod);
        buttonSearch = findViewById(R.id.buttonSearch);
        textViewResults = findViewById(R.id.textViewResults);
        recyclerViewResults = findViewById(R.id.recyclerViewResults);
        buttonView = findViewById(R.id.buttonView);
        buttonBack = findViewById(R.id.buttonBack);

        // Set up view button
        buttonView.setOnClickListener(new View.OnClickListener() {
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

                    ViewFragment viewFragment = ViewFragment.newInstance(lotNumber, name, category, period, description, picture, video);
                    replaceFragment(viewFragment);
                }
            }
        });

        // Set up back button
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Handle back press using OnBackPressedCallback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Initialize item list and adapter
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList, this, recyclerViewResults);
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewResults.setAdapter(itemAdapter);

        // Initialize Firebase
        db = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/");
        itemsRef = db.getReference("Items");

        // Set up search button click listener
        buttonSearch.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String lotNumber = editTextLotNumber.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String category = editTextCategory.getText().toString().trim();
        String period = editTextPeriod.getText().toString().trim();

        Log.d("SearchActivity", "Performing search with filters: " +
                "LotNumber=" + lotNumber + ", Name=" + name + ", Category=" + category + ", Period=" + period);

        boolean isAnyFieldFilled = !TextUtils.isEmpty(lotNumber) || !TextUtils.isEmpty(name) || !TextUtils.isEmpty(category) || !TextUtils.isEmpty(period);

        if (!isAnyFieldFilled) {
            // Fetch all data if no field is filled
            fetchAllItems();
        } else {
            // Fetch filtered data based on the filled fields
            fetchFilteredItems(lotNumber, name, category, period);
        }
    }

    private void fetchAllItems() {
        Log.d("SearchActivity", "Fetching all items from database.");
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = safeDataSnapshotToItem(dataSnapshot);
                    if (item != null) {
                        itemList.add(item);
                        Log.d("SearchActivity", "Item fetched: " + item.getName());
                    }
                }
                itemAdapter.notifyDataSetChanged();
                updateResultsMessage("", "", "", "");
                Log.d("SearchActivity", "Total items fetched: " + itemList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private void fetchFilteredItems(String lotNumber, String name, String category, String period) {
        Log.d("SearchActivity", "Fetching filtered items from database.");
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Item item = safeDataSnapshotToItem(dataSnapshot);
                    if (item != null && matchesFilters(item, lotNumber, name, category, period)) {
                        itemList.add(item);
                        Log.d("SearchActivity", "Filtered item fetched: " + item.getName());
                    }
                }
                itemAdapter.notifyDataSetChanged();
                updateResultsMessage(lotNumber, name, category, period);
                Log.d("SearchActivity", "Total filtered items fetched: " + itemList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private Item safeDataSnapshotToItem(DataSnapshot dataSnapshot) {
        try {
            return dataSnapshot.getValue(Item.class);
        } catch (DatabaseException e) {
            Log.e("SearchActivity", "Data conversion error: " + e.getMessage());
            return null;
        }
    }

    private boolean matchesFilters(Item item, String lotNumber, String name, String category, String period) {
        if (!TextUtils.isEmpty(lotNumber) && !matchesFiltersUnique(item, lotNumber)) {
            return false;
        }
        if (!TextUtils.isEmpty(name) && !item.getName().toLowerCase().contains(name.toLowerCase())) {
            return false;
        }
        if (!TextUtils.isEmpty(category) && !item.getCategory().toLowerCase().contains(category.toLowerCase())) {
            return false;
        }
        if (!TextUtils.isEmpty(period) && !item.getPeriod().toLowerCase().contains(period.toLowerCase())) {
            return false;
        }
        return true;
    }

    private boolean matchesFiltersUnique(Item item, String lotNumber) {
        return String.valueOf(item.getLotNumber()).equals(lotNumber);
    }

    private void updateResultsMessage(String lotNumber, String name, String category, String period) {
        StringBuilder message = new StringBuilder();
        message.append("Filtered Results: ").append(itemList.size()).append("\n");
        if (!TextUtils.isEmpty(lotNumber)) {
            message.append("Lot# = ").append(lotNumber).append("; ");
        }
        if (!TextUtils.isEmpty(name)) {
            message.append("Name = ").append(name).append("; ");
        }
        if (!TextUtils.isEmpty(category)) {
            message.append("Category = ").append(category).append("; ");
        }
        if (!TextUtils.isEmpty(period)) {
            message.append("Period = ").append(period).append("; ");
        }
        textViewResults.setText(message.toString());
    }

    @Override
    public void onItemSelected(Item item) {
        // Handle item selection if needed
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
