package org.nikhilp.projectlumen.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.nisrulz.sensey.ChopDetector;
import com.github.nisrulz.sensey.Sensey;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import org.nikhilp.projectlumen.R;
import org.nikhilp.projectlumen.services.BackgroundService;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout clayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!isFlashAvailable) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle(getString(R.string.app_name));
            alert.setMessage("Error");
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.show();
        }

        Intent i = new Intent(this, BackgroundService.class);
        startService(i);
        Log.d("ProjectLumen","loaded");

    }

    public void fabricInit(){
        Fabric.with(this, new Crashlytics());
    }

    @Override
    public void onBackPressed() {

    }


    public void onDestroy() {
        super.onDestroy();
        Sensey.getInstance().stop();
    }
}
