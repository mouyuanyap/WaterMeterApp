package com.example.watermeterapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BuildingUIActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(findViewById(R.id.toolbar))



        val api = Api.create(this, true)


        //snackBar
    }



    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {

        val navHostFragment = supportFragmentManager.primaryNavigationFragment
        val currentFragment = navHostFragment!!.childFragmentManager.fragments[0]

        if (currentFragment.javaClass.simpleName == "FirstFragment"){

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

            Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        }else{
            super.onBackPressed()
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        val sessionManager = SessionManager(this)
        if(item.itemId == R.id.logoutMenu){
            sessionManager.deleteAuthToken()
            val intent = Intent(this, MainActivity::class.java).apply{
            }
            startActivity(intent)
            finish()
        }

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}