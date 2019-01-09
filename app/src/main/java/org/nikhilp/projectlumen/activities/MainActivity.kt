package org.nikhilp.projectlumen.activities

import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout

import android.util.Log

import androidx.appcompat.app.AppCompatActivity

import com.github.nisrulz.sensey.Sensey
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

import org.nikhilp.projectlumen.R
import org.nikhilp.projectlumen.services.BackgroundService

class MainActivity : AppCompatActivity() {

    private val clayout: CoordinatorLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val isFlashAvailable = applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
        if (!isFlashAvailable) {
            val alert = AlertDialog.Builder(this@MainActivity).create()
            alert.setTitle(getString(R.string.app_name))
            alert.setMessage("Error")
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK") { _, _ -> finish() }
            alert.show()
        }

        val i = Intent(this, BackgroundService::class.java)
        startService(i)
        Log.d("ProjectLumen", "loaded")

    }

    fun fabricInit() {
        Fabric.with(this, Crashlytics())
    }

    override fun onBackPressed() {

    }


    public override fun onDestroy() {
        super.onDestroy()
        Sensey.getInstance().stop()
    }
}
