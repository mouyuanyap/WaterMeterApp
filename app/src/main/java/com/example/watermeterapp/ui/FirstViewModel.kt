package com.example.watermeterapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watermeterapp.Api
import com.example.watermeterapp.data.BuildingReturn
import com.example.watermeterapp.data.Buildings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FirstViewModel(app: Application): AndroidViewModel(app) {
    val buildingDetails =  MutableLiveData<List<Buildings>>()

    fun fetchBuildingDetails(): MutableLiveData<List<Buildings>> {

        viewModelScope.launch(Dispatchers.Default){
            val context = getApplication<Application>().applicationContext
            var api = Api.create(context,true)

            api.fetchAllBuildings().enqueue(object: Callback<BuildingReturn> {
                override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                    Log.d("ya", response.body().toString())
                    buildingDetails.value = response.body()?.Buildings

                }
                override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                    Log.d("yap", t.message)
                    api = Api.create(context,false)
                    api.fetchAllBuildings().enqueue(object: Callback<BuildingReturn> {
                        override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                            Log.d("ya", response.body().toString())
                            buildingDetails.value = response.body()?.Buildings
                        }

                        override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                            Log.d("yap", t.message)
                        }

                    })
                }

            })

        }
        return buildingDetails
    }
}