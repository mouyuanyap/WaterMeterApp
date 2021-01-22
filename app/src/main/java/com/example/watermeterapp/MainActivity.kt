package com.example.watermeterapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.watermeterapp.data.BuildingReturn
import com.example.watermeterapp.data.LoginRequest
import com.example.watermeterapp.data.LoginResponse
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(findViewById(R.id.toolbar))

        val api = Api.create(this,true)
        val sessionManager = SessionManager(this)



        api.fetchAllBuildings().enqueue(object : Callback<BuildingReturn> {
            override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {

                if(response?.body()?.Buildings != null){
                    val intent = Intent(this@MainActivity,BuildingUIActivity::class.java).apply{

                    }
                    startActivity(intent)
                    finish()
                }

            }

            override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                Log.d("login","not logged in yet")
            }

        })



        findViewById<Button>(R.id.loginSubmitButton).setOnClickListener{
            var inputUsername:String = findViewById<EditText>(R.id.editTextUsername).text.toString()
            var inputPassword:String = findViewById<EditText>(R.id.editTextPassword).text.toString()
            Log.d("username",inputUsername)
            Log.d("password", inputPassword)
            var loginRequest = LoginRequest(inputUsername,inputPassword)
            api.login(loginRequest)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.d("onFailure",t.message)
                        Snackbar.make(
                            findViewById(R.id.loginLayout),
                            t.message.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        // Error logging in
                    }

                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        val login_response = response.body()
                        //if (login_response?.statusCode == 200 &&

                        if (login_response?.id != null) {
                            sessionManager.saveAuthToken(login_response.token)
                            sessionManager.saveUserID(login_response.id)
                            var message = login_response.id.toString()
                            val intent = Intent(this@MainActivity,BuildingUIActivity::class.java).apply{

                            }
                            startActivity(intent)
                            finish()

                        } else {
                            Log.d("onResponse","failed")
                            Snackbar.make(
                                findViewById(R.id.loginLayout),
                                "Error Login.",
                                Snackbar.LENGTH_LONG
                            ).show()
                            // Error logging in
                        }
                    }
                })
        }



        //snackBar



    }
/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    */
}