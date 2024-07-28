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
    private EditText editTextTextLot;
    private Button submitButtonLot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);

        editTextTextLot = findViewById(R.id.editTextTextLot);
        submitButtonLot = findViewById(R.id.submitButtonLot);
        submitButtonLot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdf();
            }
        });
    }
    /*
    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with PDF generation
                // You may need to call createPdf() here
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

     */
    private void generatePdf() {
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
}
