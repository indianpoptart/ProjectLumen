package org.nikhilp.projectlumen.activities

import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout

import android.util.Log
import android.widget.SeekBar

import androidx.appcompat.app.AppCompatActivity

import com.github.nisrulz.sensey.Sensey
import com.google.android.material.snackbar.Snackbar

import org.nikhilp.projectlumen.R
import org.nikhilp.projectlumen.services.BackgroundService

class MainActivity : AppCompatActivity() {

    private lateinit var clayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clayout = findViewById(R.id.clayout)

        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = prefs.edit()

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

        val seekBar = findViewById<SeekBar>(R.id.seekBar2)
        seekBar.progress = prefs.getInt("sensitivity", 3)
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                editor.putInt("sensitivity", progress)
                editor.apply()
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Write code to perform some action when touch is stopped.
                Snackbar.make(clayout, "Sensitivity is set to " + seekBar.progress, Snackbar.LENGTH_LONG)
                        .show()
            }
        })
        editor.apply()
    }

    override fun onBackPressed() {

    }


    public override fun onDestroy() {
        super.onDestroy()
        Sensey.getInstance().stop()
    }
}
