package com.allVideosTo.PdfConverter7830.Fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.Manifest;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.allVideosTo.PdfConverter7830.R;
import com.allVideosTo.PdfConverter7830.pdfview;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class EditFragment<BufferedImage> extends Fragment {
    private static final int PICK_PDF_REQUEST = 1;
    private static final int PICK_PDF = 20;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 233;
    TextView test,textedit;
    private static final int PICK_IMAGES_REQUEST = 2;
    private ArrayList<Uri> selectedPdfs = new ArrayList<>();
    private ArrayList<Uri>  selectedImagesSequence = new ArrayList<>();
    private CardView mergeButton, imagebutton;
    private Dialog dialog;
    TextView dialogtext;
    ProgressBar progressBar;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        mergeButton = view.findViewById(R.id.merge);
        imagebutton = view.findViewById(R.id.imageButton);
        test = view.findViewById(R.id.test);
        textedit = view.findViewById(R.id.textedit);
        //ad mob
        loadaAd();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            textedit.setTextSize(20);
            textedit.setText("All Videos To PDF Converter");
        }

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                super.onAdClicked();

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError);
                mAdView.loadAd(adRequest);
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });


        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.progressbar);
        progressBar = dialog.findViewById(R.id.progressloading);
        dialogtext = dialog.findViewById(R.id.custometext);
        dialog.setCancelable(false);

        mergeButton.setOnClickListener(new View.OnClickListener() {

            private void handleAdAndPicker() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show((Activity) requireContext());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            openPdfPicker();
                            loadaAd();
                        }
                    });
                } else {
                    openPdfPicker();
                }
            }
            @Override
            public void onClick(View v) {
                handleAdAndPicker();
            }
        });

        imagebutton.setOnClickListener(new View.OnClickListener() {
            private void handleAdAndPickerImage() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show((Activity) requireContext());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            openimagepicker();
                            loadaAd();
                        }
                    });
                } else {
                    openimagepicker();
                }
            }

            @Override
            public void onClick(View v) {

                handleAdAndPickerImage();

                }
        });

        return view;
    }


    private void openPdfPicker() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select PDFs"), PICK_PDF_REQUEST);
        dialogtext.setText("Merging PDF");
        dialog.show();

    }

    private void openimagepicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select images"), PICK_IMAGES_REQUEST);
        dialogtext.setText("Image to PDF");
        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        selectedPdfs.add(uri);
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    selectedPdfs.add(uri);
                }

                // Merge the selected PDFs
                mergeSelectedPDFs();
            }

        }
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            // Handle image selection and save the sequence
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        selectedImagesSequence.add(imageUri);
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    selectedImagesSequence.add(uri);
                }

                // Create a PDF from the saved sequence of selected images
                createPDFFromImages(selectedImagesSequence);

            }

        }

        if (requestCode == PICK_PDF && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedPdfUri = data.getData();
//            splitSelectedPDF(selectedPdfUri);
        }

        dialog.dismiss();
    }


    private void mergeSelectedPDFs() {
        if (selectedPdfs.size() < 2) {
            Toast.makeText(requireContext(), "Select at least two PDFs to merge.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Define the folder in the app's private external storage
            File folder = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PDFs");
            if (!folder.exists()) {
                folder.mkdirs(); // Create the folder if it doesn't exist
            }

            // Generate the file name and full path
            String timestamp = generateTimestamp();
            String pdfFileName = "MergedPDF_" + timestamp + ".pdf";
            File outputFile = new File(folder, pdfFileName);

            // Create the document and file output stream for merging PDFs
            Document document = new Document();
            FileOutputStream fos = new FileOutputStream(outputFile);
            PdfCopy copy = new PdfCopy(document, fos);
            document.open();

            // Loop through selected PDFs and merge them
            for (Uri pdfUri : selectedPdfs) {
                PdfReader reader = new PdfReader(requireContext().getContentResolver().openInputStream(pdfUri));
                int numPages = reader.getNumberOfPages();

                for (int pageNum = 1; pageNum <= numPages; pageNum++) {
                    copy.addPage(copy.getImportedPage(reader, pageNum));
                }
            }

            // Close the document and streams
            document.close();
            fos.close();
            copy.close();

            // Load the PDF files from the new location and clear cache if necessary
            loadPdfFilesFromExternalStorage();
            clearCache();
            dialog.dismiss();
        } catch (IOException | DocumentException e) {
            Toast.makeText(requireContext(), "Error merging PDFs.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private String generateTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void clearCache() {
        File cacheDir = requireContext().getCacheDir();
        if (cacheDir != null && cacheDir.isDirectory()) {
            String[] children = cacheDir.list();
            for (String child : children) {
                new File(cacheDir, child).delete();
            }
        }
    }

    //image to pdf

    private void createPDFFromImages(ArrayList<Uri> selectedImages) {
        try {
            File outputDirectory = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PDFs");
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs(); // Create the directory if it doesn't exist
            }

            String timestamp = generateTimestamp();
            String pdfFileName = "ImagesToPDF_" + timestamp + ".pdf";
            File outputFile = new File(outputDirectory, pdfFileName);


//            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            FileOutputStream fos = new FileOutputStream(outputFile);
            PdfWriter.getInstance(document, fos);
            document.open();
            ContentResolver contentResolver = requireContext().getContentResolver();
            // Yahan 'cover_page' aapke Drawable ka naam hoga

            // Create a temporary file for the cover page
            File coverPageFile = new File(requireContext().getCacheDir(), "cover_page.png");
            Drawable coverPageDrawable = getResources().getDrawable(R.drawable.portaitnew);
            Bitmap coverPageBitmap = ((BitmapDrawable) coverPageDrawable).getBitmap();
            try {
                FileOutputStream coverPageStream = new FileOutputStream(coverPageFile);
                coverPageBitmap.compress(Bitmap.CompressFormat.PNG, 100, coverPageStream);
                coverPageStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            com.itextpdf.text.Image coverPageImage = com.itextpdf.text.Image.getInstance(coverPageFile.getAbsolutePath());
            coverPageImage.scaleToFit(document.getPageSize().getWidth(),document.getPageSize().getHeight());
            coverPageImage.setAlignment(Element.ALIGN_CENTER);
            document.add(coverPageImage);

            for (Uri imageUri : selectedImages) {
                try {
                    // Open an input stream for the image URI
                    InputStream imageStream = contentResolver.openInputStream(imageUri);

                    if (imageStream != null) {
                        com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(readBytesFromStream(imageStream));
                        document.setPageSize(PageSize.A4); // Set a fixed page size for A4

// Calculate the scaling factor to fit the image to the page size while maintaining aspect ratio
                        float widthScale = PageSize.A4.getWidth() / image.getWidth();
                        float heightScale = PageSize.A4.getHeight() / image.getHeight();
                        float scale = Math.min(widthScale, heightScale);

                        // Calculate the X and Y coordinates to center the image on the A4 page
                        float xPosition = (PageSize.A4.getWidth() - (image.getWidth() * scale)) / 2;
                        float yPosition = (PageSize.A4.getHeight() - (image.getHeight() * scale)) / 2;

                        image.scaleAbsolute(image.getWidth() * scale, image.getHeight() * scale);
                        image.setAbsolutePosition(xPosition, yPosition);
                        document.newPage();
                        document.add(image);
                        imageStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            document.close();
            fos.close();
            dialog.dismiss();
            loadPdfFilesFromExternalStorage();
            clearCache();
        } catch (IOException | DocumentException e) {
            test.setText(e.getMessage().toString());
            e.printStackTrace();
        }
    }

    private byte[] readBytesFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
    public  void loadaAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(requireContext(),"ca-app-pub-5242787336207828/8042313994", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }
    private void loadPdfFilesFromExternalStorage() {
        // Define the directory where your PDF files are stored in the app's private external storage
        File directory = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PDFs");

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                Arrays.sort(files, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return Long.compare(f2.lastModified(), f1.lastModified()); // Sort by most recent first
                    }
                });

                // Assuming 'open' is used to store the latest file
                File open = files[0];
                Intent intent = new Intent(requireContext(), pdfview.class);
                intent.putExtra("link", open.toString());
                startActivity(intent);
            }
        }
    }

}
