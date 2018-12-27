package rolf.nomnoms.nomnoms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.schedule


class ActivitySplash : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
    }

    public override fun onStart() {
        super.onStart()


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            startMainActivity()
            return
        }

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1234)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if( requestCode == 1234 ){
            if( grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                startMainActivity()
                return
            }
            else
                finish()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPause() {
        super.onPause()
        if( isFinishing )
            overridePendingTransition(R.anim.fade_in_100, R.anim.fade_out_100)
    }


    private fun startMainActivity(){
        Timer("StartMainActivity", false).schedule(250) {
            val intent = Intent(this@ActivitySplash, ActivityNoms::class.java)
            startActivity(intent)
            finish()

        }
    }
}