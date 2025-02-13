package com.mohdmustaqeem3040.allvideostopdfconverter.Fragment;
import static android.app.Activity.RESULT_OK;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mohdmustaqeem3040.allvideostopdfconverter.MainActivity;
import com.mohdmustaqeem3040.allvideostopdfconverter.PdfGenerator;
import com.mohdmustaqeem3040.allvideostopdfconverter.R;
import com.mohdmustaqeem3040.allvideostopdfconverter.RealPathUtil;
import com.mohdmustaqeem3040.allvideostopdfconverter.pdfview;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.common.IntentSenderForResultStarter;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.itextpdf.text.Image;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import android.Manifest;

public class HomeFragment extends Fragment {
    private static final int INTERNET_PERMISSION_REQUEST_CODE = 123;
    private static final int REQUEST = 121;
    //    Button selectbtn,multiple;
    CardView select, multiple, cardView2;
    TextView tester;
    String type = "0";
    int quality = 25;
    private Dialog qualityDialog;
    int REQUEST_CODE = 100;
    int SINGLE_REQUEST_CODE = 102;
    private MediaMetadataRetriever retriever;
    private Handler handler = new Handler();
    private long frameCaptureInterval = 10000; // 10 seconds in milliseconds
    private List<File> capturedFrames = new ArrayList<>();
    private int currentFrameIndex = 0;

    private boolean capturingFrames = true;
    private long duration;
    Dialog dialog;
    TextView text, texthome;
    Toast toast;
    ProgressBar progressBar;
    List<Uri> selectedVideos;
    Spinner timeIntervalSpinner;
    ArrayList<String> list;
    private AdView mAdView;
    AppUpdateManager appUpdateManager;
    private RewardedAd rewardedAd;
    File open;
    private InterstitialAd mInterstitialAd;

    @SuppressLint("MissingInflatedId")


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        select = view.findViewById(R.id.select);
        tester = view.findViewById(R.id.tester);
        cardView2 = view.findViewById(R.id.cardView);
        texthome = view.findViewById(R.id.texthome);
        multiple = view.findViewById(R.id.multiple);
        qualityDialog = new Dialog(requireContext());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            texthome.setTextSize(20);
            texthome.setText("All Videos To PDF Converter");
        }

        loadreward();
        loadaAd();
        //update
        checkupdate();


        //admob id

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                // Ad clicked logic (optional)
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                // Ad closed logic (optional)
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                super.onAdFailedToLoad(adError);
                // Retry loading the ad with a delay to avoid excessive requests
                new Handler(Looper.getMainLooper()).postDelayed(() -> mAdView.loadAd(adRequest), 3000); // Retry after 3 seconds
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // Ad successfully loaded logic (optional)
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                // Ad opened logic (optional)
            }
        });

        selectedVideos = new ArrayList<>();
        retriever = new MediaMetadataRetriever();
        timeIntervalSpinner = view.findViewById(R.id.timeIntervalSpinner);

        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.progressbar);
        progressBar = dialog.findViewById(R.id.progressloading);
        dialog.setCancelable(false);
        try {

            @SuppressLint("ResourceType") InputStream coverImageStream = requireContext().getResources().openRawResource(R.drawable.portaitnew);
            Image coverImage = Image.getInstance(coverImageStream.toString());
            Toast.makeText(requireContext(), (CharSequence) coverImage, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        LayoutInflater inflat = getLayoutInflater();
        View layout = inflat.inflate(R.layout.customtoast, null);

        toast = new Toast(requireContext());
        toast.setDuration(Toast.LENGTH_LONG); // or Toast.LENGTH_LONG
        toast.setView(layout);
        text = layout.findViewById(R.id.toast_text);


        list = new ArrayList<>();
        list.add("Select Time Interval");
        list.add("Custom Time");
        list.add("5    Second");
        list.add("10  Second");
        list.add("20  Second");
        list.add("30  Second");
        list.add("1    minute");
        list.add("5    minute");
        list.add("10    minute");


        ArrayAdapter arrayAdapter = new ArrayAdapter(requireContext(), R.layout.textview, list);
        timeIntervalSpinner.setAdapter(arrayAdapter);
        timeIntervalSpinner.setAdapter(arrayAdapter);

// Set the initial selection (prompt) as the default item at position 0
        timeIntervalSpinner.setSelection(0, false); // false prevents it from triggering the listener

        timeIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0) { // Check if the user selects an item other than the prompt
                    String selectedInterval = timeIntervalSpinner.getSelectedItem().toString();
                    switch (position) {

                        case 1: {
                            customSetTime();
                            break;
                        }
                        case 2: {
                            frameCaptureInterval = 5000;
                            break;
                        }
                        case 3: {
                            frameCaptureInterval = 10000;
                            break;
                        }
                        case 4: {
                            frameCaptureInterval = 20000;
                            break;
                        }
                        case 5: {
                            frameCaptureInterval = 30000;
                            break;
                        }
                        case 6: {
                            frameCaptureInterval = 60000;
                            break;
                        }
                        case 7: {
                            frameCaptureInterval = 300000;
                            break;
                        }
                        case 8: {
                            frameCaptureInterval = 600000;
                            break;
                        }
                    }
                    // Use the selectedInterval for your frame capture logic
                    Toast.makeText(requireContext(), "Selected Time Interval: " + selectedInterval, Toast.LENGTH_SHORT).show();
                } else {
                    frameCaptureInterval = 10000;
                    tester.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissioncheck();
                type = "1";
            }


        });
        multiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissioncheck();
                type = "2";
            }
        });
        return view;
    }


        private void selectVideo() {
            dialog.show();
            qualityDialog.dismiss();
            capturingFrames = true;
            capturedFrames.clear();
            timeIntervalSpinner.getFirstVisiblePosition();
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, SINGLE_REQUEST_CODE);
        }

        private void multipleSelectVideo() {
            qualityDialog.dismiss();
            dialog.show();
            capturingFrames = true;
            capturedFrames.clear();
            timeIntervalSpinner.getFirstVisiblePosition();

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("video/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Enable multiple selection
            startActivityForResult(intent, REQUEST_CODE);
        }


    private class FrameCaptureTask extends AsyncTask<File, Void, List<File>> {
        @Override
        protected void onPreExecute() {
            dialog.show(); // Show a progress dialog
        }

        @Override
        protected List<File> doInBackground(File... files) {
            List<File> frames = new ArrayList<>();
            // Process each file and capture frames
            frames.add(saveCoverImage());
            for (File file : files) {
                String videoPath = file.getAbsolutePath();
                File cacheDir = requireContext().getCacheDir();
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                frames.addAll(captureFrames(videoPath, cacheDir));
            }
            // Update the first frame with another image if required
            if (!frames.isEmpty()) {
                frames.set(0, saveCoverImageafter(frames));
            }
            return frames;
        }

        @Override
        protected void onPostExecute(List<File> frames) {
            try {
                generatePDFFromFrames(frames);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            resetSpinnerPosition();
            capturedFrames.clear();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) {
            if (dialog != null) dialog.dismiss();
            return;
        }

        if (requestCode == SINGLE_REQUEST_CODE) {
            handleSingleRequest(data);
        } else if (requestCode == REQUEST_CODE) {
            handleMultipleRequests(data);
        }
    }

    private void handleSingleRequest(Intent data) {
        Uri videoUri = data.getData();
        if (videoUri != null) {
            String realPath = RealPathUtil.getRealPath(requireContext(), videoUri);
            if (realPath != null) {
                List<File> files = new ArrayList<>();
                files.add(new File(realPath));
                new FrameCaptureTask().execute(files.toArray(new File[0]));
            } else {
                Toast.makeText(requireContext(), "Unable to retrieve video file", Toast.LENGTH_SHORT).show();
                if (dialog != null) dialog.dismiss();
            }
        } else {
            if (dialog != null) dialog.dismiss();
        }
    }

    private void handleMultipleRequests(Intent data) {
        ClipData clipData = data.getClipData();
        if (clipData != null && clipData.getItemCount() > 1) {
            List<File> files = getFilesFromURIs(clipData);
            new FrameCaptureTask().execute(files.toArray(new File[0]));
        } else {
            Toast.makeText(requireContext(), "Please select at least 2 videos", Toast.LENGTH_SHORT).show();
            if (dialog != null) dialog.dismiss();
        }
    }




    private List<File> getFilesFromURIs(ClipData clipData) {
        List<File> files = new ArrayList<>();

        // Step 1: Clean up existing temporary files
        File tempDir = requireContext().getFilesDir();
        File[] tempFiles = tempDir.listFiles((dir, name) -> name.startsWith("temp_video_") && name.endsWith(".mp4"));
        if (tempFiles != null) {
            for (File tempFile : tempFiles) {
               tempFile.delete();
            }
        }

        // Step 2: Process new URIs and create new temporary files
        for (int i = 0; i < clipData.getItemCount(); i++) {
            Uri uri = clipData.getItemAt(i).getUri();
            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(new File(tempDir, "temp_video_" + i + ".mp4"))) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                File tempFile = new File(tempDir, "temp_video_" + i + ".mp4");
                files.add(tempFile);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error processing file: " + uri.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        return files;
    }

    //new code
    private List<File> captureFrames(String videoPath, File outputDirectory) {
        retriever.setDataSource(videoPath);

        String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Long.parseLong(durationStr); // Convert to milliseconds

        List<File> frames = new ArrayList<>();

        long captureTime = 0;
        while (captureTime < duration) {
            Bitmap frame = retriever.getFrameAtTime(captureTime * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

            if (frame != null) {
                // Save the frame as an image file
                String framePath = outputDirectory.getAbsolutePath() + File.separator + "frame_" + currentFrameIndex + ".jpg";
                saveBitmapAsImageFile(frame, framePath);
                frames.add(new File(framePath));
                currentFrameIndex++;
            }
            captureTime += frameCaptureInterval;
        }
        return frames;
    }

    private void saveBitmapAsImageFile(Bitmap bitmap, String filePath) {
        try {
            FileOutputStream out = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //spinner reset after 10 second
    private void resetSpinnerPosition() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                timeIntervalSpinner.setSelection(0); // Set Spinner to the first item
            }
        }, 5000); // Delayed for 5 seconds (5,000 milliseconds)
    }

    private void generatePDFFromFrames(List<File> frames) throws Exception {
        if (!frames.isEmpty()) {
            String pdfFileName = "Videos PDF_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".pdf";
            PdfGenerator.generatePDF(requireContext(), frames, pdfFileName);

            Intent intent = new Intent(requireContext(), pdfview.class);
            loadPdfFilesFromExternalStorage();
            intent.putExtra("link", open.toString());
            startActivity(intent);
            text.setText("PDF created successfully check into saved PDF");
            toast.show();
            dialog.dismiss();
            resetSpinnerPosition();
            frames.clear(); // Clear the frames list
        }
    }

    //quality box
    private void showRenameDialog() {
        qualityDialog.setContentView(R.layout.qualitypdf);
        qualityDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = qualityDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Set width to match parent
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Set height as needed
            window.setAttributes(layoutParams);
        }
//        Button low = qualityDialog.findViewById(R.id.low);
        Button normal = qualityDialog.findViewById(R.id.normal);
        Button high = qualityDialog.findViewById(R.id.high);


        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quality = 10;
                switch (type) {
                    case "1": {
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show((Activity) requireContext());

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
//                              super.onAdDismissedFullScreenContent();
                                    selectVideo();
                                    loadaAd();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    mInterstitialAd = null;
                                }
                            });
                        } else {
                            selectVideo();
                        }
                        break;
                    }
                    case "2": {
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show((Activity) requireContext());

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
//                              super.onAdDismissedFullScreenContent();
                                    multipleSelectVideo();
                                    loadaAd();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    mInterstitialAd = null;
                                }

                            });
                        } else {
                            multipleSelectVideo();

                        }
                        break;
                    }
                }

            }
        });
        high.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quality = 100;
                switch (type) {
                    case "1": {

                        if (rewardedAd != null) {
                            Activity activityContext = (Activity) requireContext();
                            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    loadreward();
                                }
                            });
                            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    selectVideo();
                                    loadreward();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    selectVideo();
                                    startActivity(new Intent(activityContext, MainActivity.class));
                                    Toast.makeText(activityContext, "Poor Internet Connection", Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onAdImpression() {
                                    super.onAdImpression();
                                }
                            });
                        } else {
                            selectVideo();

                        }
                        break;
                    }
                    case "2": {

                        if (rewardedAd != null) {
                            Activity activityContext = (Activity) requireContext();
                            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    loadreward();
                                }
                            });
                            rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdClicked() {
                                    super.onAdClicked();
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    multipleSelectVideo();
                                    loadreward();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    startActivity(new Intent(activityContext, MainActivity.class));
                                    Toast.makeText(activityContext, "Poor Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            multipleSelectVideo();

                        }
                        break;
                    }
                }

            }
        });
        qualityDialog.show();
    }

    //code for cover image
    private File saveCoverImage() {

        // Load the appropriate cover image based on dimensions
        Drawable coverImageDrawable = null;
        coverImageDrawable = getResources().getDrawable(R.drawable.covernew);


        if (coverImageDrawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) coverImageDrawable).getBitmap();
            File outputDirectory = requireContext().getCacheDir();
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            String filePath = outputDirectory.getAbsolutePath() + File.separator + "cover_image.jpg";
            saveBitmapAsImageFile(bitmap, filePath);
            return new File(filePath);
        }
        return null;
    }

    //code for change the cover image according to the size of the image second
    private File saveCoverImageafter(List<File> frames) {
        // Load the second frame's dimensions
        int width = 0;
        int height = 0;

        if (frames.size() > 1) {

            if (!frames.isEmpty()) {
                File firstImageFile = frames.get(1);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(firstImageFile.getAbsolutePath(), options);
                width = options.outWidth;
                height = options.outHeight;
            }


        }

        // Load the appropriate cover image based on dimensions
        Drawable coverImageDrawable = null;

        if (width > height) {
            coverImageDrawable = getResources().getDrawable(R.drawable.covernew);
        } else {
            coverImageDrawable = getResources().getDrawable(R.drawable.portaitnew);
        }

        if (coverImageDrawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) coverImageDrawable).getBitmap();
            File outputDirectory = requireContext().getCacheDir();
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            String filePath = outputDirectory.getAbsolutePath() + File.separator + "cover_image.jpg";
            saveBitmapAsImageFile(bitmap, filePath);
            return new File(filePath);
        }
        return null;
    }

    public void loadaAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(requireContext(), "ca-app-pub-5242787336207828/8042313994", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        mInterstitialAd = null;
                    }
                });
    }

    public void loadreward() {
        AdRequest Request = new AdRequest.Builder().build();
        RewardedAd.load(requireContext(), "ca-app-pub-5242787336207828/3225935736",
                Request, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        rewardedAd = null;

                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }
                });
    }


    private void loadPdfFilesFromExternalStorage() {
        // Define the directory where PDFs are saved
        File directory = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PDFs");

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null && files.length > 0) {
                // Sort files by last modified date in descending order
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return Long.compare(f2.lastModified(), f1.lastModified()); // Most recent first
                    }
                });

                // Assign the most recent file to the 'open' variable
                open = files[0];
            }
        }
    }

    private void checkupdate() {
        appUpdateManager = AppUpdateManagerFactory.create(requireContext());

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // an activity result launcher registered via registerForActivityResult
                            AppUpdateType.FLEXIBLE, (IntentSenderForResultStarter) this,
                            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                            // flexible updates.
                            REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        appUpdateManager.registerListener(listener);


    }

    InstallStateUpdatedListener listener = state -> {
        // (Optional) Provide a download progress bar.
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            long bytesDownloaded = state.bytesDownloaded();
            long totalBytesToDownload = state.totalBytesToDownload();
            // Implement progress bar.
        }
        // Log state or install the update.
    };

    private void popupSnackbarForCompleteUpdate() {
        View rootView = getView(); // Get the root view of the fragment

        if (rootView != null) {
            Snackbar snackbar = Snackbar.make(rootView,
                    "An update has just been downloaded.",
                    Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("INSTALL", view -> appUpdateManager.completeUpdate());
            snackbar.setActionTextColor(
                    getResources().getColor(android.R.color.white));
            snackbar.show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        appUpdateManager.unregisterListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.


                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate();
                    }
                });
    }

    private void permissioncheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 and above
            showRenameDialog();
        } else {
            texthome.setTextSize(20);
            texthome.setText("All Videos To PDF Converter");
            cardView2.setPadding(3, 3, 3, 3);
                showRenameDialog();
        }

    }


    private void customSetTime() {
        Dialog renameDialog = new Dialog(requireContext());
        renameDialog.setContentView(R.layout.rename);
        renameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = renameDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // Set width to match parent
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // Set height as needed
            window.setAttributes(layoutParams);
        }

        Button buttonRename = renameDialog.findViewById(R.id.btnrename);
        Button buttonCancel = renameDialog.findViewById(R.id.btncancel);
        TextInputEditText newname = renameDialog.findViewById(R.id.renametext);
        TextInputLayout textInputLayout = renameDialog.findViewById(R.id.edtrename); // Reference to TextInputLayout
        textInputLayout.setHint("Enter the time in second");
        newname.setInputType(InputType.TYPE_CLASS_NUMBER); // Only allow numeric input
        newname.setKeyListener(DigitsKeyListener.getInstance("0123456789")); // Allow only digits
        buttonRename.setText("Set");
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                renameDialog.dismiss();
                resetSpinnerPosition();
            }
        });

        buttonRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int second = Integer.parseInt(newname.getText().toString());
                if (second!=0){
                    frameCaptureInterval = second*1000;
                    Toast.makeText(requireContext(), "Selected Time Interval: " + newname.getText().toString()+" seconds", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(requireContext(),"Time second can't be zero", Toast.LENGTH_SHORT).show();
                    resetSpinnerPosition();

                }
                 renameDialog.dismiss();
            }
        });

        renameDialog.show();
    }

}