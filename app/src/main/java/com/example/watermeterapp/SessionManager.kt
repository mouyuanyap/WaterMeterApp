package com.example.watermeterapp

import android.content.Context
import android.content.SharedPreferences

/**
 * Session manager to save and fetch data from SharedPreferences
 */
class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN = "user_token"
        const val USER_ID ="user_id"
        const val USER_LAST_NAME = "user_last_name"
    }

    fun saveUserLastName(Lname:String){
        val editor = prefs.edit()
        editor.putString(USER_LAST_NAME,Lname)
        editor.apply()
    }

    fun fetchUserLastName():String?{
        return prefs.getString(USER_LAST_NAME,"")
    }

    fun saveUserID(id:Int){
        val editor = prefs.edit()
        editor.putInt(USER_ID,id)
        editor.apply()
    }

    fun fetchUserID():Int?{
        return prefs.getInt(USER_ID,-1)
    }

    /**
     * Function to save auth token
     */
    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun deleteAuthToken(){
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    /**
     * Function to fetch auth token
     */
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
}