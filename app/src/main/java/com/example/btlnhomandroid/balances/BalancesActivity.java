package com.example.btlnhomandroid.balances;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btlnhomandroid.Nanuda;
import com.example.btlnhomandroid.R;
import com.example.btlnhomandroid.expense.ExpensesListActivity;
import com.example.btlnhomandroid.objects.DetailsListObject;
import com.example.btlnhomandroid.objects.Expense;
import com.example.btlnhomandroid.objects.Group;
import com.example.btlnhomandroid.objects.SummaryListObject;
import com.google.android.material.tabs.TabLayout;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Handles behaviour for the Balances Activity.
 */
public class BalancesActivity extends AppCompatActivity {

    private Group group;
    private ArrayList<Expense> expenses;
    private ArrayList<SummaryListObject> summaryList = new ArrayList<>();
    private ArrayList<DetailsListObject> detailsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balances);

        Intent intent = getIntent();
        group = intent.getParcelableExtra(Nanuda.EXTRA_GROUP);
        expenses = intent.getParcelableArrayListExtra(Nanuda.EXTRA_EXPENSES);

        // Select Balances tab by default
        TabLayout tabLayout = findViewById(R.id.balancesTabLayout);
        TabLayout.Tab balancesTab = tabLayout.getTabAt(1);
        if (balancesTab != null) {
            balancesTab.select();
        }

        // Add On Tab Selected Listener to "Expenses" tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    Intent backIntent = setUpBackIntent(true);
                    startActivity(backIntent);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Set up toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Balances");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setUpLists(group, expenses);

        RecyclerView recyclerView = findViewById(R.id.balancesList);
        BalancesAdapter balancesAdapter = new BalancesAdapter();
        balancesAdapter.setSummaryList(summaryList);
        balancesAdapter.setDetailsList(detailsList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(balancesAdapter);


        ActivityResultLauncher<Intent> manageStorageLauncher;
        manageStorageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Kiểm tra quyền sau khi người dùng cấp quyền
                        if (Environment.isExternalStorageManager()) {
                            exportToPdf();  // Nếu quyền đã được cấp, xuất PDF
                        } else {
                            Toast.makeText(this, "Cần cấp quyền truy cập bộ nhớ ngoài để lưu file", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Export to PDF button
        Button exportPdfButton = findViewById(R.id.exportPdfButton);
        exportPdfButton.setOnClickListener(v -> {
                // Nếu phiên bản Android 11 trở lên, kiểm tra quyền MANAGE_EXTERNAL_STORAGE
                if (Environment.isExternalStorageManager()) {
                    // Nếu quyền đã được cấp, thực hiện xuất PDF
                    exportToPdf();
                } else {
                    Toast.makeText(this, "Cần cấp quyền truy cập bộ nhớ ngoài để lưu file", Toast.LENGTH_SHORT).show();
                    // Nếu quyền chưa được cấp, yêu cầu quyền
                    Intent intentt = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intentt.setData(Uri.parse("package:" + getPackageName()));
                    manageStorageLauncher.launch(intentt);
                }
        });
    }
    private final ActivityResultLauncher<Intent> manageStorageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (Environment.isExternalStorageManager()) {
                    exportToPdf(); // Nếu quyền được cấp, thực hiện lưu PDF
                } else {
                    Toast.makeText(this, "Cần cấp quyền truy cập bộ nhớ ngoài để lưu file!", Toast.LENGTH_SHORT).show();
                }
            }
    );
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportToPdf();
            } else {
                Toast.makeText(this, "Cần cấp quyền để lưu file PDF!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpLists(Group group, ArrayList<Expense> expenses) {
        HashMap<String, Long> summaries = new HashMap<>();
        List<String> participants = group.getParticipants();

        // Initialize summary hashmap
        for (String participant : participants) {
            summaries.put(participant, 0L);
        }

        // Count summaries
        for (Expense expense : expenses) {
            String payer = expense.getPayer();
            long payerSum = summaries.get(payer) + expense.getAmount();
            summaries.put(payer, payerSum);

            List<String> payees = expense.getPayees();
            List<Integer> owedAmounts = expense.getOwedAmounts();

            for (int i = 0; i < payees.size(); i++) {
                String payee = payees.get(i);
                long payeeSum = summaries.get(payee) - owedAmounts.get(i);
                summaries.put(payee, payeeSum);
            }
        }

        List<SummaryListObject> posSummaries = new ArrayList<>();
        List<SummaryListObject> negSummaries = new ArrayList<>();

        // Assign summary list
        for (String participant : participants) {
            long sum = summaries.get(participant);
            SummaryListObject summary = new SummaryListObject(participant, sum, group.getCurrency());
            summaryList.add(summary);

            if (sum > 0) {
                posSummaries.add(summary);
            } else if (sum < 0) {
                negSummaries.add(summary);
            }
        }

        posSummaries.sort(Collections.reverseOrder());
        Collections.sort(negSummaries);

        // Assign details list
        int negIndex = 0;
        long negRemains = 0;
        String currOwerName = "";

        for (SummaryListObject posSummary : posSummaries) {
            long posSum = posSummary.getParticipantSum();
            String oweeName = posSummary.getParticipantName();

            if (negRemains >= 0) {
                negRemains = negSummaries.get(negIndex).getParticipantSum();
                currOwerName = negSummaries.get(negIndex).getParticipantName();
            }

            while (posSum > 0) {
                if (posSum + negRemains <= 0) {
                    negRemains += posSum;
                    detailsList.add(new DetailsListObject(currOwerName, oweeName, Math.abs(posSum), group.getCurrency()));
                    posSum = 0;
                } else {
                    posSum += negRemains;
                    detailsList.add(new DetailsListObject(currOwerName, oweeName, Math.abs(negRemains), group.getCurrency()));
                    negIndex++;
                    if (negIndex < negSummaries.size()) {
                        negRemains = negSummaries.get(negIndex).getParticipantSum();
                        currOwerName = negSummaries.get(negIndex).getParticipantName();
                    }
                }
            }
        }
    }

    private Intent setUpBackIntent(boolean toParentActivity) {
        Intent intent;
        if (toParentActivity) {
            intent = new Intent(this, ExpensesListActivity.class);
        } else {
            intent = new Intent();
        }

        intent.putExtra(Nanuda.EXTRA_GROUP, group);
        intent.putParcelableArrayListExtra(Nanuda.EXTRA_EXPENSES, expenses);
        return intent;
    }

    @Override
    public void onBackPressed() {
        Intent intent = setUpBackIntent();
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
    private Intent setUpBackIntent() {
        Intent intent = new Intent();
        intent.putExtra(Nanuda.EXTRA_GROUP, group);
        intent.putParcelableArrayListExtra(Nanuda.EXTRA_EXPENSES, expenses);
        return intent;
    }

    // Khi người dùng nhấn nút home/back trong toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Quay lại màn hình trước khi nhấn nút home
            Intent intent = setUpBackIntent();
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Hàm xử lý việc xuất dữ liệu ra PDF khi người dùng nhấn nút xuất
    @SuppressLint("NewApi")
    private void exportToPdf() {
        // Kiểm tra quyền truy cập bộ nhớ ngoài
        if (Environment.isExternalStorageManager()) {
            // Nếu đã có quyền truy cập, tiến hành xuất file PDF
            try {
                // Lưu trữ PDF vào thư mục công cộng Documents
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Balances_" + System.currentTimeMillis() + ".pdf");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/BalancesPDFs");

                // Lấy URI của file
                Uri pdfUri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

                if (pdfUri != null) {
                    // Mở OutputStream để ghi nội dung vào file
                    OutputStream outputStream = getContentResolver().openOutputStream(pdfUri);
                    if (outputStream != null) {
                        PdfWriter writer = new PdfWriter(outputStream);
                        com.itextpdf.kernel.pdf.PdfDocument pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(writer);
                        Document document = new Document(pdfDocument);

                        document.add(new Paragraph("Expense Report").setBold().setFontSize(16));
                        document.add(new Paragraph("\nDetails of Expenses:").setBold());

                        for (Expense expense : expenses) {
                            document.add(new Paragraph("- " + expense.getTitle() + ": " + expense.getAmount() + " " + group.getCurrency()));
                        }

                        document.add(new Paragraph("\n\nPayment Suggestions:").setBold());
                        for (DetailsListObject detail : detailsList) {
                            document.add(new Paragraph("- " + detail.getOwerName() + " pays " + detail.getOwedAmount() + " " + group.getCurrency() + " to " + detail.getOweeName()));
                        }

                        document.close();
                        outputStream.close();
                        Toast.makeText(this, "Đã lưu file: " + pdfUri, Toast.LENGTH_SHORT).show();
                        Log.d("PDF Export", "PDF saved at: " + pdfUri);
                        openPdf(pdfUri);
                    } else {
                        Log.e("PDF Export", "Error opening output stream.");
                    }
                } else {
                    Log.e("PDF Export", "Error inserting PDF into MediaStore.");
                }
            } catch (Exception e) {
                Log.e("PDF Export", "Error exporting PDF: " + e.getMessage());
            }
        } else {
            // Nếu không có quyền, yêu cầu quyền
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, 100);
        }
    }

    // Mở file PDF sau khi nó đã được tạo và lưu thành công
    @SuppressLint("QueryPermissionsNeeded")
    private void openPdf(Uri pdfUri) {
        try {
            // Tạo Intent để mở PDF trong ứng dụng quản lý tệp hoặc ứng dụng hỗ trợ PDF.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(pdfUri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Chọn ứng dụng để mở PDF"));
        } catch (Exception e) {
            // Xử lý lỗi nếu có sự cố khi mở PDF
            Log.e("Open PDF", "Error opening PDF: " + e.getMessage());
            Toast.makeText(this, "Có lỗi khi mở PDF!", Toast.LENGTH_SHORT).show();
        }
    }
}