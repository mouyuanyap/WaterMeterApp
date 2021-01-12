package com.example.watermeterapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.watermeterapp.Api
import com.example.watermeterapp.R
import com.example.watermeterapp.data.*
import com.example.watermeterapp.database.AppDatabase
import com.example.watermeterapp.database.RecordDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SecondViewModel(app:Application): AndroidViewModel(app) {
    val recordDetails = MutableLiveData<List<Records>>()
    val usageDetails = MutableLiveData<List<Usages>>()
    val usageAverage = MutableLiveData<List<UsageAverage>>()
    val recordDb = MutableLiveData<List<RecordDB>>()
    val network = MutableLiveData<Boolean>()


    val context: Context = getApplication<Application>().applicationContext

    var api = Api.create(context,true)

    var db = AppDatabase.getAppDataBase(context)
    var recDao = db?.recordDao()


    fun deleteAllDB(){
        viewModelScope.launch(Dispatchers.Default) {
            recDao?.deleteAll()
        }
    }

    fun hasNetwork(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    fun fetchNetworkStatus(){
        if(hasNetwork(context)){
            var api = Api.create(context,true)
            api.fetchAllBuildings().enqueue(object:Callback<BuildingReturn>{
                override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                    Log.d("net","true")
                    network.value = true
                }

                override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                    network.value = false
                    Log.d("net","flasee")
                }

            })
        }else{
            network.value = false
        }

    }


    fun insertRecordDB(propertyID: Int,reading:Int){
        var temp = RecordDB(0,propertyID,reading)
        recDao?.insertRecord(temp)
        Log.d("roomI",recDao?.getAll().toString())

    }



    fun fetchRecordDB(propertyID: Int):MutableLiveData<List<RecordDB>>{
        var temp:List<RecordDB>? = null
        temp = recDao?.getSpecific(propertyID)
            //recordDb.postValue(recDao?.getSpecific(propertyID))

        recordDb.value = temp

        Log.d("roomF",recDao?.getAll().toString())
        return recordDb
    }


    fun fetchRecordDetails(propertyID: Int):MutableLiveData<List<Records>>{
        viewModelScope.launch(Dispatchers.Default) {

            api = Api.create(context,true)
            api.fetchSpecificBuildings(propertyID).enqueue(object: Callback<RecordReturn> {
                override fun onResponse(call: Call<RecordReturn>, response: Response<RecordReturn>) {

                    Log.d("important!",response.body()?.Records.toString())
                    recordDetails.value = response.body()?.Records

                }
                override fun onFailure(call: Call<RecordReturn>, t: Throwable) {
                    api = Api.create(context,false)
                    api.fetchSpecificBuildings(propertyID).enqueue(object: Callback<RecordReturn> {
                        override fun onResponse(call: Call<RecordReturn>, response: Response<RecordReturn>) {
                            Log.d("important@!",response.body()?.Records.toString())
                            recordDetails.value = response.body()?.Records
                        }

                        override fun onFailure(call: Call<RecordReturn>, t: Throwable) {
                            Log.d("aa",t.message)
                        }

                    })
                }

            })

        }
        return recordDetails
    }

    fun fetchUsageDetails(propertyID: Int):MutableLiveData<List<Usages>>{
        viewModelScope.launch(Dispatchers.Default) {


            api = Api.create(context,true)
            api.fetchSpecificUsage(propertyID).enqueue(object :Callback<UsageReturn>{
                override fun onResponse(call: Call<UsageReturn>, response: Response<UsageReturn>) {

                    usageDetails.value = response.body()?.Usage

                }

                override fun onFailure(call: Call<UsageReturn>, t: Throwable) {
                    Log.d("Yap",t.message)
                    api = Api.create(context,false)
                    api.fetchSpecificUsage(propertyID).enqueue(object :Callback<UsageReturn>{
                        override fun onResponse(call: Call<UsageReturn>, response: Response<UsageReturn>) {
                            usageDetails.value = response.body()?.Usage
                        }

                        override fun onFailure(call: Call<UsageReturn>, t: Throwable) {
                            Log.d("Yap",t.message)
                        }

                    })
                }

            })
        }
        return usageDetails

    }

    fun fetchUsageAverage(propertyID: Int):MutableLiveData<List<UsageAverage>>{
        viewModelScope.launch(Dispatchers.Default) {
            api = Api.create(context,true)
            api.fetchSpecificUsageAverage(propertyID).enqueue(object :Callback<UsageAvgReturn>{
                override fun onResponse(call: Call<UsageAvgReturn>, response: Response<UsageAvgReturn>) {

                    usageAverage.value = response.body()?.UsageAvg

                }

                override fun onFailure(call: Call<UsageAvgReturn>, t: Throwable) {
                    Log.d("Yap",t.message)
                    api = Api.create(context,false)
                    api.fetchSpecificUsageAverage(propertyID).enqueue(object :Callback<UsageAvgReturn>{
                        override fun onResponse(call: Call<UsageAvgReturn>, response: Response<UsageAvgReturn>) {
                            usageAverage.value = response.body()?.UsageAvg
                        }

                        override fun onFailure(call: Call<UsageAvgReturn>, t: Throwable) {
                            Log.d("Yap",t.message)
                        }

                    })
                }

            })
        }
        return usageAverage

    }

}