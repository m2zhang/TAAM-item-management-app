package com.example.b07demosummer2024;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
    private Button buttonSearch;
    private TextView textViewResults;
    private RecyclerView recyclerViewResults;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;

    private FirebaseDatabase db;
    private DatabaseReference itemsRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        // Initialize UI elements
        editTextLotNumber = findViewById(R.id.editTextLotNumber);
        editTextName = findViewById(R.id.editTextName);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextPeriod = findViewById(R.id.editTextPeriod);
        buttonSearch = findViewById(R.id.buttonSearch);
        textViewResults = findViewById(R.id.textViewResults);
        recyclerViewResults = findViewById(R.id.recyclerViewResults);

        // Initialize item list and adapter
        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList, this);
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
                textViewResults.setText("Total Results: " + itemList.size());
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
                textViewResults.setText("Filtered Results: " + itemList.size());
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
        if (!TextUtils.isEmpty(lotNumber) && item.getLotNumber() != Integer.parseInt(lotNumber)) {
            return false;
        }
        if (!TextUtils.isEmpty(name) && !item.getName().equalsIgnoreCase(name)) {
            return false;
        }
        if (!TextUtils.isEmpty(category) && !item.getCategory().equalsIgnoreCase(category)) {
            return false;
        }
        if (!TextUtils.isEmpty(period) && !item.getPeriod().equalsIgnoreCase(period)) {
            return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(Item item) {
        // Handle item selection if needed
    }

    public void goBackHome(View view) {
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
