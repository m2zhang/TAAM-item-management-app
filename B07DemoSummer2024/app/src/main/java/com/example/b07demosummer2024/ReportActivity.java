package com.example.b07demosummer2024;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReportActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 112;
    private EditText editTextTextLot;
    private Button submitButtonLot;
    private EditText editTextTextName;
    private Button submitButtonName;
    private EditText editTextTextCategory;
    private Button submitButtonCategory;
    private EditText editTextTextPeriod;
    private Button submitButtonPeriod;
    private Button submitButtonAllItems;
    private EditText editTextTextPeriodDescPic;
    private Button submitButtonPeriodDescPic;
    private EditText editTextTextCategoryDescPic;
    private Button submitButtonCategoryDescPic;
    private Button submitButtonAllItemsDescPic;
    private Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        editTextTextLot = findViewById(R.id.editTextTextLot);
        submitButtonLot = findViewById(R.id.submitButtonLot);
        editTextTextName = findViewById(R.id.editTextTextName);
        submitButtonName = findViewById(R.id.submitButtonName);
        editTextTextCategory = findViewById(R.id.editTextTextCategory);
        submitButtonCategory = findViewById(R.id.submitButtonCategory);
        editTextTextCategoryDescPic = findViewById(R.id.editTextTextCategoryDescPic);
        submitButtonCategoryDescPic = findViewById(R.id.submitButtonCategoryDescPic);
        editTextTextPeriod = findViewById(R.id.editTextTextPeriod);
        submitButtonPeriod = findViewById(R.id.submitButtonPeriod);
        editTextTextPeriodDescPic = findViewById(R.id.editTextTextPeriodDescPic);
        submitButtonPeriodDescPic = findViewById(R.id.submitButtonPeriodDescPic);
        submitButtonAllItemsDescPic = findViewById(R.id.submitButtonAllItemsDescPic);
        submitButtonAllItems = findViewById(R.id.submitButtonAllItems);
        buttonBack = findViewById(R.id.buttonBack);

        submitButtonLot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfLot();
            }
        });

        submitButtonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfName();
            }
        });

        submitButtonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfCategory();
            }
        });

        submitButtonCategoryDescPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfCategoryDescPic();
            }
        });


        submitButtonPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfPeriod();
            }
        });

        submitButtonPeriodDescPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfPeriodDescPic();
            }
        });

        submitButtonAllItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfAll();
            }
        });

        submitButtonAllItemsDescPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfAllDescPic();
            }
        });


        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void generatePdfLot() {
        String lotNumber = editTextTextLot.getText().toString().trim();

        if (lotNumber.isEmpty()) {
            Toast.makeText(this, "Please make an entry", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference itemsRef = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/")
                .getReference("Items");

        itemsRef.orderByChild("lotNumber").equalTo(Integer.parseInt(lotNumber)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File file = new File(path, "Report_lot_number.pdf");
                        PdfWriter writer = new PdfWriter(file);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf);

                        document.add(new Paragraph("Report for Lot Number: " + lotNumber));

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Item item = dataSnapshot.getValue(Item.class);
                            if (item != null) {
                                document.add(new Paragraph("Lot Number: " + item.getLotNumber()));
                                document.add(new Paragraph("Name: " + item.getName()));
                                document.add(new Paragraph("Category: " + item.getCategory()));
                                document.add(new Paragraph("Period: " + item.getPeriod()));
                                document.add(new Paragraph("Description: " + item.getDescription()));


                                // picture not loading properly
                                /* if (item.getPicture() != null && !item.getPicture().isEmpty()) {
                                    ImageData imageData = ImageDataFactory.create(item.getPicture());
                                    Image image = new Image(imageData);
                                    document.add(image);
                                } else {
                                    document.add(new Paragraph("No picture found"));
                                }*/


                            }
                        }

                        document.close();
                        Toast.makeText(ReportActivity.this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ReportActivity.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportActivity.this, "No items found with the specified lot number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void generatePdfName() {
        String itemName = editTextTextName.getText().toString().trim();

        if (itemName.isEmpty()) {
            Toast.makeText(this, "Please make an entry", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference itemsRef = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/")
                .getReference("Items");

        itemsRef.orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File file = new File(path, "Report_name.pdf");
                        PdfWriter writer = new PdfWriter(file);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf);

                        document.add(new Paragraph("Report for Item Name: " + itemName));

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Item item = dataSnapshot.getValue(Item.class);
                            if (item != null) {
                                document.add(new Paragraph("Lot Number: " + item.getLotNumber()));
                                document.add(new Paragraph("Name: " + item.getName()));
                                document.add(new Paragraph("Category: " + item.getCategory()));
                                document.add(new Paragraph("Period: " + item.getPeriod()));
                                document.add(new Paragraph("Description: " + item.getDescription()));

                                // picture not loading properly
                                /*if (item.getPicture() != null && !item.getPicture().isEmpty()) {
                                    ImageData imageData = ImageDataFactory.create(item.getPicture());
                                    Image image = new Image(imageData);
                                    document.add(image);
                                } else {
                                    document.add(new Paragraph("No picture found"));
                                }*/
                            }
                        }

                        document.close();
                        Toast.makeText(ReportActivity.this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ReportActivity.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportActivity.this, "No items found with the specified name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void generatePdfCategory() {
        String itemCategory = editTextTextCategory.getText().toString().trim();

        if (itemCategory.isEmpty()) {
            Toast.makeText(this, "Please make an entry", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference itemsRef = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/")
                .getReference("Items");

        itemsRef.orderByChild("category").equalTo(itemCategory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File file = new File(path, "Report_category.pdf");
                        PdfWriter writer = new PdfWriter(file);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf);

                        document.add(new Paragraph("Report for Category: " + itemCategory));

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Item item = dataSnapshot.getValue(Item.class);
                            if (item != null) {
                                document.add(new Paragraph("Lot Number: " + item.getLotNumber()));
                                document.add(new Paragraph("Name: " + item.getName()));
                                document.add(new Paragraph("Category: " + item.getCategory()));
                                document.add(new Paragraph("Period: " + item.getPeriod()));
                                document.add(new Paragraph("Description: " + item.getDescription()));

                                // picture not loading properly
                                /*if (item.getPicture() != null && !item.getPicture().isEmpty()) {
                                    ImageData imageData = ImageDataFactory.create(item.getPicture());
                                    Image image = new Image(imageData);
                                    document.add(image);
                                } else {
                                    document.add(new Paragraph("No picture found"));
                                }*/
                            }
                        }

                        document.close();
                        Toast.makeText(ReportActivity.this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ReportActivity.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportActivity.this, "No items found with the specified category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generatePdfCategoryDescPic() {
        String itemCategory = editTextTextCategoryDescPic.getText().toString().trim();

        if (itemCategory.isEmpty()) {
            Toast.makeText(this, "Please make an entry", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference itemsRef = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/")
                .getReference("Items");

        itemsRef.orderByChild("category").equalTo(itemCategory).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File file = new File(path, "Report_by_category_desc_pic.pdf");
                        PdfWriter writer = new PdfWriter(file);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf);

                        document.add(new Paragraph("Report for Category with Description and Picture only: " + itemCategory));

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Item item = dataSnapshot.getValue(Item.class);
                            if (item != null) {
                                document.add(new Paragraph("Lot Number: " + item.getLotNumber()));
                                document.add(new Paragraph("Description: " + item.getDescription()));

                                // picture not loading properly
                                /*if (item.getPicture() != null && !item.getPicture().isEmpty()) {
                                    ImageData imageData = ImageDataFactory.create(item.getPicture());
                                    Image image = new Image(imageData);
                                    document.add(image);
                                } else {
                                    document.add(new Paragraph("No picture found"));
                                }*/
                            }
                        }

                        document.close();
                        Toast.makeText(ReportActivity.this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ReportActivity.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportActivity.this, "No items found with the specified category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void generatePdfPeriod() {
        String itemPeriod = editTextTextPeriod.getText().toString().trim();

        if (itemPeriod.isEmpty()) {
            Toast.makeText(this, "Please make an entry", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference itemsRef = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/")
                .getReference("Items");

        itemsRef.orderByChild("period").equalTo(itemPeriod).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File file = new File(path, "Report_by_period.pdf");
                        PdfWriter writer = new PdfWriter(file);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf);

                        document.add(new Paragraph("Report for Period: " + itemPeriod));

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Item item = dataSnapshot.getValue(Item.class);
                            if (item != null) {
                                document.add(new Paragraph("Lot Number: " + item.getLotNumber()));
                                document.add(new Paragraph("Name: " + item.getName()));
                                document.add(new Paragraph("Category: " + item.getCategory()));
                                document.add(new Paragraph("Period: " + item.getPeriod()));
                                document.add(new Paragraph("Description: " + item.getDescription()));

                                // picture not loading properly
                                /*if (item.getPicture() != null && !item.getPicture().isEmpty()) {
                                    ImageData imageData = ImageDataFactory.create(item.getPicture());
                                    Image image = new Image(imageData);
                                    document.add(image);
                                } else {
                                    document.add(new Paragraph("No picture found"));
                                }*/
                            }
                        }

                        document.close();
                        Toast.makeText(ReportActivity.this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ReportActivity.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportActivity.this, "No items found with the specified period", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generatePdfPeriodDescPic() {
        String itemPeriod = editTextTextPeriodDescPic.getText().toString().trim();

        if (itemPeriod.isEmpty()) {
            Toast.makeText(this, "Please make an entry", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference itemsRef = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/")
                .getReference("Items");

        itemsRef.orderByChild("period").equalTo(itemPeriod).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File file = new File(path, "Report_by_period_desc_pic.pdf");
                        PdfWriter writer = new PdfWriter(file);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf);

                        document.add(new Paragraph("Report for Period with Description and Picture only: " + itemPeriod));

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Item item = dataSnapshot.getValue(Item.class);
                            if (item != null) {
                                document.add(new Paragraph("Lot Number: " + item.getLotNumber()));
                                document.add(new Paragraph("Description: " + item.getDescription()));

                                // picture not loading properly
                                /*if (item.getPicture() != null && !item.getPicture().isEmpty()) {
                                    ImageData imageData = ImageDataFactory.create(item.getPicture());
                                    Image image = new Image(imageData);
                                    document.add(image);
                                } else {
                                    document.add(new Paragraph("No picture found"));
                                }*/
                            }
                        }

                        document.close();
                        Toast.makeText(ReportActivity.this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ReportActivity.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportActivity.this, "No items found with the specified period", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void generatePdfAll() {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/")
                .getReference("Items");

        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File file = new File(path, "Report_all.pdf");
                        PdfWriter writer = new PdfWriter(file);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf);

                        document.add(new Paragraph("Report for All Items"));

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Item item = dataSnapshot.getValue(Item.class);
                            if (item != null) {
                                document.add(new Paragraph("Lot Number: " + item.getLotNumber()));
                                document.add(new Paragraph("Name: " + item.getName()));
                                document.add(new Paragraph("Category: " + item.getCategory()));
                                document.add(new Paragraph("Period: " + item.getPeriod()));
                                document.add(new Paragraph("Description: " + item.getDescription()));

                                // Picture loading (uncomment and fix this section if needed)
                            /*if (item.getPicture() != null && !item.getPicture().isEmpty()) {
                                new DownloadImageTask(document).execute(item.getPicture());
                            } else {
                                document.add(new Paragraph("No picture found"));
                            }*/
                            }
                        }

                        document.close();
                        Toast.makeText(ReportActivity.this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ReportActivity.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportActivity.this, "No items found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generatePdfAllDescPic() {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance("https://b07-project-c1ef0-default-rtdb.firebaseio.com/")
                .getReference("Items");

        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                        File file = new File(path, "Report_all_desc_pic.pdf");
                        PdfWriter writer = new PdfWriter(file);
                        PdfDocument pdf = new PdfDocument(writer);
                        Document document = new Document(pdf);

                        document.add(new Paragraph("Report for All Items with Description and Picture only"));

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Item item = dataSnapshot.getValue(Item.class);
                            if (item != null) {
                                document.add(new Paragraph("Lot Number: " + item.getLotNumber()));
                                document.add(new Paragraph("Description: " + item.getDescription()));

                                // Picture loading (uncomment and fix this section if needed)
                            /*if (item.getPicture() != null && !item.getPicture().isEmpty()) {
                                new DownloadImageTask(document).execute(item.getPicture());
                            } else {
                                document.add(new Paragraph("No picture found"));
                            }*/
                            }
                        }

                        document.close();
                        Toast.makeText(ReportActivity.this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ReportActivity.this, "Error generating PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ReportActivity.this, "No items found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
