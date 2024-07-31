package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText editTextLotNumber, editTextName, editTextCategory, editTextPeriod;
    private Button buttonSearch;
    private TextView textViewResults;
    private RecyclerView recyclerViewResults;
    private List<Item> itemList;
    private ItemAdapter itemAdapter;

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
        itemAdapter = new ItemAdapter(itemList);
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewResults.setAdapter(itemAdapter);

        // Handle search button click
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lotNumber = editTextLotNumber.getText().toString().trim();
                String name = editTextName.getText().toString().trim();
                String category = editTextCategory.getText().toString().trim();
                String period = editTextPeriod.getText().toString().trim();

                searchItems(lotNumber, name, category, period);
            }
        });
    }

    private void searchItems(String lotNumber, String name, String category, String period) {
        // Perform search (this is just a dummy implementation)
        List<Item> results = new ArrayList<>();
        for (Item item : itemList) {
            if ((lotNumber.isEmpty() /*|| item.getLotNumber().contains(lotNumber)*/) &&
                    (name.isEmpty() || item.getName().contains(name)) &&
                    (category.isEmpty() || item.getCategory().contains(category)) &&
                    (period.isEmpty() || item.getPeriod().contains(period))) {
                results.add(item);
            }
        }

        // Update UI with results
        textViewResults.setText(results.size() + " results found");
        itemAdapter.updateList(results);
    }
}
