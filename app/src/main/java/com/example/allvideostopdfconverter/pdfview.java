package com.example.allvideostopdfconverter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;

import java.io.File;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import android.widget.TextView;
import java.io.File;

public class pdfview extends AppCompatActivity {
    PDFView pdfView;
    TextView pageNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        String url = getIntent().getStringExtra("link");
        pdfView = findViewById(R.id.pdfView);
        pageNumberTextView = findViewById(R.id.pageNumberTextView);

        pdfView.fromFile(new File(url))
                .defaultPage(0)
                .enableSwipe(true)
                .enableDoubletap(true)
                .swipeHorizontal(false)

        .onRender(new OnRenderListener() {
            @Override
            public void onInitiallyRendered(int nbPages) {
                pdfView.fitToWidth(0); // optionally pass page number
            }
        })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        // Set the page number text and update its position
                        pageNumberTextView.setText("Total " + pageCount);
                    }
                })
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }
}
