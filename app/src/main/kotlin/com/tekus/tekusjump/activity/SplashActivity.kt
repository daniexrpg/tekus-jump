package com.tekus.tekusjump.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.tekus.tekusjump.R

/**
 * Created by Jose Daniel on 20/01/2017.
 */
class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        delay()
    }

    private fun delay() {
        Handler().postDelayed({
            goToMain()
        }, 1500)
    }

    private fun goToMain() {
        val mIntent: Intent = Intent(this, MainActivity::class.java)
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(mIntent)
        finish()
    }
}