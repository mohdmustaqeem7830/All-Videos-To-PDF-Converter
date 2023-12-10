package com.mohdmustaqeem3040.allvideostopdfconverter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mohdmustaqeem3040.allvideostopdfconverter.R;
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
    private static final int REQUEST =121 ;
    int i = 0;
    AlertDialog.Builder builder;
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

        //code internet connection
        // Start the periodic task
        handler.post(periodicTask);

        bottomBar = findViewById(R.id.bottomBar);

        //by default smoothbar at home
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentByTag("HomeFragment");
//        if (homeFragment == null) {
//            // If not added, add it
//            homeFragment = new HomeFragment();
//            fragmentTransaction.replace(R.id.fragmentContainer, homeFragment, "HomeFragment");
//        }
//        fragmentTransaction.commit();
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Use findFragmentByTag to check if the fragment is already added
                HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentByTag("HomeFragment");


                if (i == 0) {
                    // If not added, add HomeFragment
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
    private Runnable periodicTask = new Runnable() {
        @Override
        public void run() {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                // No internet connection, show a message or close the app.
                Toast.makeText(MainActivity.this, "Please open the internet connection", Toast.LENGTH_SHORT).show();
                finish(); // This will close the app
            }
            // Schedule the task to run again after 3 seconds
            handler.postDelayed(this, 3000); // 3000 milliseconds = 3 seconds
        }
    };

}