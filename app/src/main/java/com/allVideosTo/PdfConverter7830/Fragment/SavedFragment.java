package com.allVideosTo.PdfConverter7830.Fragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.allVideosTo.PdfConverter7830.PdfAdapter;
import com.allVideosTo.PdfConverter7830.R;
import com.allVideosTo.PdfConverter7830.pdfview;
import com.google.android.gms.ads.AdView;

import android.os.Environment;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
public class SavedFragment extends Fragment  implements PdfAdapter.OnItemLongClickListener {
    private PdfAdapter pdfAdapter;

    private AdView mAdView;
    Button deleteall;
    private ArrayList<File> pdfFiles = new ArrayList<>();
    LinearLayout emptypdf;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View  view =  inflater.inflate(R.layout.fragment_saved, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        emptypdf = view.findViewById(R.id.emptypdf);
        deleteall =view.findViewById(R.id.deleteall);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        pdfAdapter = new PdfAdapter(pdfFiles,requireContext());
        pdfAdapter.setOnItemClickListener(this::onItemClick);
        pdfAdapter.setOnItemLongClickListener(this); // 'this' refers to the fragment
        recyclerView.setAdapter(pdfAdapter);
        loadPdfFilesFromExternalStorage();
// ad mob
//        mAdView = view.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//        mAdView.setAdListener(new AdListener() {
//
//            @Override
//            public void onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//                super.onAdClicked();
//                mAdView.loadAd(adRequest);
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when the user is about to return
//                // to the app after tapping on an ad.
//                super.onAdClosed();
//                mAdView.loadAd(adRequest);
//            }
//
//            @Override
//            public void onAdFailedToLoad(LoadAdError adError) {
//                // Code to be executed when an ad request fails.
//                super.onAdFailedToLoad(adError);
//                mAdView.loadAd(adRequest);
//            }
//
//            @Override
//            public void onAdImpression() {
//                super.onAdImpression();
//                // Code to be executed when an impression is recorded
//                // for an ad.
//            }
//
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//                super.onAdLoaded();
//                mAdView.loadAd(adRequest);
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//                super.onAdOpened();
//            }
//        });

        deleteall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });



        return view;
    }
    private void loadPdfFilesFromExternalStorage() {
        // Define the directory where your PDF files are stored in external storage
        File directory = new File(Environment.getExternalStorageDirectory(), "/android/media/Videos To PDF");

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                Arrays.sort(files, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return Long.compare(f2.lastModified(), f1.lastModified()); // Sort by most recent first
                    }
                });

                pdfFiles.clear(); // Clear the list

                for (File file : files) {
                    if (file.getName().endsWith(".pdf")) {
                        pdfFiles.add(file);
                    }
                }
              if (pdfFiles.size()==0){
                  emptypdf.setVisibility(View.VISIBLE);
              }
                pdfAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void onItemClick(int position) {

    }



    @Override
    public void onItemClick(String pdfFilePath) {
        Intent intent = new Intent(requireContext(), pdfview.class);
        intent.putExtra("link",pdfFilePath);
        startActivity(intent);
        // Handle the item click here, e.g., store the selected PDF file path, open the PDF, etc.
    }
    public void deleteAllPdf() {
        for (int i = pdfFiles.size() - 1; i >= 0; i--) {
            File pdfFile = pdfFiles.get(i);
            if (pdfFile.delete()) {
                // Successfully deleted the file from storage
                pdfFiles.remove(i);
                emptypdf.setVisibility(View.VISIBLE);
                pdfAdapter.notifyItemRemoved(i);
            }
        }
        pdfAdapter.notifyDataSetChanged();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete All PDF");
        builder.setMessage("Are you sure want to delete all the PDF files ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User confirmed to delete
                        deleteAllPdf();
                        emptypdf.setVisibility(View.VISIBLE);
                        pdfAdapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User canceled the delete operation

                    }
                });

        builder.create().show();
    }

}