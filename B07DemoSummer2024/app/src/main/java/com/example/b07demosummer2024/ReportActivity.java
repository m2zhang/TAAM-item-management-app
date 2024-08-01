package com.example.b07demosummer2024;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.FileOutputStream;
import java.io.IOException;

public class ReportActivity extends AppCompatActivity{

    private static final int REQUEST_WRITE_STORAGE = 112;
    // Initializing variables for simple functions
    private EditText editTextTextLot;
    private Button submitButtonLot;
    private EditText editTextTextName;
    private Button submitButtonName;
    private EditText editTextTextCategory;
    private Button submitButtonCategory;
    private EditText editTextTextPeriod;
    private Button submitButtonPeriod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);
        // Assigning variables for simple functions
        editTextTextLot = findViewById(R.id.editTextTextLot);
        submitButtonLot = findViewById(R.id.submitButtonLot);
        editTextTextName = findViewById(R.id.editTextTextName);
        submitButtonName = findViewById(R.id.submitButtonName);
        editTextTextCategory = findViewById(R.id.editTextTextCategory);
        submitButtonCategory = findViewById(R.id.submitButtonCategory);
        editTextTextPeriod = findViewById(R.id.editTextTextPeriod);
        submitButtonPeriod = findViewById(R.id.submitButtonPeriod);


        // setOnClickListeners for simple functions
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
        submitButtonPeriod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdfPeriod();
            }
        });

    }
    //generatePdf methods for simple functions
    private void generatePdfLot() {
        String itemName = editTextTextLot.getText().toString();

        if (itemName.isEmpty()) {
            Toast.makeText(this, "Please make an entry", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create a PdfWriter
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            File file = new File(path, "Report.pdf");
            PdfWriter writer = new PdfWriter(file);

            // Create a PdfDocument
            PdfDocument pdf = new PdfDocument(writer);

            // Create a Document
            Document document = new Document(pdf);

            // Add a paragraph
            document.add(new Paragraph("Report: " + itemName));

            // Close the document
            document.close();

            Toast.makeText(this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }
    private void generatePdfName() {
        String itemName = editTextTextName.getText().toString();

        if (itemName.isEmpty()) {
            Toast.makeText(this, "Please make an entry", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create a PdfWriter
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            File file = new File(path, "Report.pdf");
            PdfWriter writer = new PdfWriter(file);

            // Create a PdfDocument
            PdfDocument pdf = new PdfDocument(writer);

            // Create a Document
            Document document = new Document(pdf);

            // Add a paragraph
            document.add(new Paragraph("Report: " + itemName));

            // Close the document
            document.close();

            Toast.makeText(this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }
    private void generatePdfCategory() {
        String itemName = editTextTextCategory.getText().toString();

        if (itemName.isEmpty()) {
            Toast.makeText(this, "Please make an entry", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create a PdfWriter
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            File file = new File(path, "Report.pdf");
            PdfWriter writer = new PdfWriter(file);

            // Create a PdfDocument
            PdfDocument pdf = new PdfDocument(writer);

            // Create a Document
            Document document = new Document(pdf);

            // Add a paragraph
            document.add(new Paragraph("Report: " + itemName));

            // Close the document
            document.close();

            Toast.makeText(this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }
    private void generatePdfPeriod() {
        String itemName = editTextTextPeriod.getText().toString();

        if (itemName.isEmpty()) {
            Toast.makeText(this, "Please make an entry", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Create a PdfWriter
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            File file = new File(path, "Report.pdf");
            PdfWriter writer = new PdfWriter(file);

            // Create a PdfDocument
            PdfDocument pdf = new PdfDocument(writer);

            // Create a Document
            Document document = new Document(pdf);

            // Add a paragraph
            document.add(new Paragraph("Report: " + itemName));

            // Close the document
            document.close();

            Toast.makeText(this, "PDF generated at: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
