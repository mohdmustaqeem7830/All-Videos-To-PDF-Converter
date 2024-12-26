package com.mohdmustaqeem3040.allvideostopdfconverter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import com.mohdmustaqeem3040.allvideostopdfconverter.Fragment.EditFragment;
import com.mohdmustaqeem3040.allvideostopdfconverter.Fragment.HelpFragment;
import com.mohdmustaqeem3040.allvideostopdfconverter.Fragment.HomeFragment;
import com.mohdmustaqeem3040.allvideostopdfconverter.Fragment.SavedFragment;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    int i = 0;
    SmoothBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseMessaging.getInstance().subscribeToTopic("notification");


        //ads mob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        handler.post(periodicTask);

        bottomBar = findViewById(R.id.bottomBar);

        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentByTag("HomeFragment");


                if (i == 0) {
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                        fragmentTransaction.replace(R.id.fragmentContainer, homeFragment, "HomeFragment");
                    }
                }
                else if (i == 1) {
                    fragmentTransaction.replace(R.id.fragmentContainer, new EditFragment());
                }
                else if (i == 2) {
                    fragmentTransaction.replace(R.id.fragmentContainer, new SavedFragment());

                }
                else if (i == 3) {
                    fragmentTransaction.replace(R.id.fragmentContainer, new HelpFragment());

                }
                fragmentTransaction.commit();
                return false;
            }
        });
    }

    private void showDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure want to Exit ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User canceled the delete operation

                    }
                });

        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }



    private Handler handler = new Handler();
    private AlertDialog internetDialog;  // Declare the dialog globally
    private boolean isInternetConnected = false; // Track internet connection status

    private Runnable periodicTask = new Runnable() {
        @Override
        public void run() {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo == null || !networkInfo.isConnected()) {
                // No internet connection, keep the dialog visible with the "Restart Application" button
                if (internetDialog == null || !internetDialog.isShowing()) {
                    showInternetDialog();  // Show the dialog with "Restart Application" button
                }
            } else {
                // Internet is available, dismiss the dialog if it's showing
                if (internetDialog != null && internetDialog.isShowing()) {
                    internetDialog.dismiss();  // Dismiss the current dialog
                }
            }

            // Schedule the task to run again after 3 seconds
            handler.postDelayed(this, 3000); // 3000 milliseconds = 3 seconds
        }
    };

    // Method to show the internet connection dialog
    private void showInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("No Internet Connection")
                .setMessage("Please open the internet connection.")
                .setCancelable(false)
                .setPositiveButton("Restart Application", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restartApplication(); // Restart the application
                    }
                });

        internetDialog = builder.create();
        internetDialog.show();
    }

    private void restartApplication() {
        try {
            // Get the launch intent for the application
            Intent intent = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());

            if (intent != null) {
                // Add flags to clear the existing task and start a new one
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            // Exit the current process cleanly
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}