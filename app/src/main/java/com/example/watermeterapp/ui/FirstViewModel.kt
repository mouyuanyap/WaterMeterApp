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
    val blockQuery = MutableLiveData<String>()
    val floorQuery = MutableLiveData<String>()
    val isOnline = MutableLiveData<Boolean>()

    val recordDetails: MutableLiveData<HashMap<Int,String>> = MutableLiveData(HashMap<Int,String>())
    val buildingName: MutableLiveData<HashMap<Int,String>> = MutableLiveData(HashMap<Int,String>())

    val context: Context = getApplication<Application>().applicationContext

    fun putRecordDetails(id:Int,time:String){
        Log.d("VMput", id.toString() + " " + time)
        var hash = recordDetails.value
        hash?.put(id,time)
        Log.d("VMput", hash.toString())
        recordDetails.value = hash
    }

    fun getBlockQuery(Block:String){
        Log.d("blockValue", Block)
        blockQuery.value = Block
    }

    fun getFloorQuery(Floor:String){
        Log.d("floorValue", Floor)
        floorQuery.value = Floor
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

    fun checkNetwork(){
        isOnline.value = hasNetwork(context)
    }

    fun fetchSpecificAllBuildingDetails(Block:String, Floor: String): MutableLiveData<List<Buildings>> {

        checkNetwork()

            val context = getApplication<Application>().applicationContext
            var api = Api.create(context,true)

            api.fetchAllBuildingsFilterAll(Block,Floor).enqueue(object: Callback<BuildingReturn> {
                override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                    isOnline.value = true
                    Log.d("filter","good")
                    for (item in response.body()?.Buildings!!) {
                        buildingName.value?.put(
                            item.UnitID,
                            item.UnitBlock + "-" + item.UnitName + "-" + item.UnitFloor
                        )
                        recordDetails.value?.put(item.UnitID,item.LastRecordTime)
                    }
                    buildingDetails.value = response.body()?.Buildings

                }

                override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                    isOnline.value = false
                    Log.d("filter",t.message)
                    api = Api.create(context,false)
                    api.fetchAllBuildingsFilterAll(Block,Floor).enqueue(object: Callback<BuildingReturn> {
                        override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                            Log.d("ya", response.body().toString())
                            for (item in response.body()?.Buildings!!) {
                                buildingName.value?.put(
                                    item.UnitID,
                                    item.UnitBlock + "-" + item.UnitName + "-" + item.UnitFloor
                                )
                                recordDetails.value?.put(item.UnitID,item.LastRecordTime)
                            }
                            buildingDetails.value = response.body()?.Buildings

                        }
                        override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                            Log.d("yap", t.message)
                        }
                    })

                }

            })


        return buildingDetails
    }

    fun fetchSpecificBlockBuildingDetails(Block:String): MutableLiveData<List<Buildings>> {

        checkNetwork()

            val context = getApplication<Application>().applicationContext
            var api = Api.create(context,true)

            api.fetchAllBuildingsFilterBlock(Block).enqueue(object: Callback<BuildingReturn> {
                override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                    Log.d("filter","good")
                    for (item in response.body()?.Buildings!!) {
                        buildingName.value?.put(
                            item.UnitID,
                            item.UnitBlock + "-" + item.UnitName + "-" + item.UnitFloor
                        )
                        recordDetails.value?.put(item.UnitID,item.LastRecordTime)
                    }
                    buildingDetails.value = response.body()?.Buildings

                    isOnline.value = true
                }

                override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                    Log.d("filter",t.message)
                    isOnline.value = false
                    api = Api.create(context,false)
                    api.fetchAllBuildingsFilterBlock(Block).enqueue(object: Callback<BuildingReturn> {
                        override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                            Log.d("ya", response.body().toString())
                            for (item in response.body()?.Buildings!!) {
                                buildingName.value?.put(
                                    item.UnitID,
                                    item.UnitBlock + "-" + item.UnitName + "-" + item.UnitFloor
                                )
                                recordDetails.value?.put(item.UnitID,item.LastRecordTime)
                            }
                            buildingDetails.value = response.body()?.Buildings

                        }

                        override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                            Log.d("yap", t.message)
                        }

                    })
                }

            })


        return buildingDetails
    }

    fun fetchSpecificFloorBuildingDetails(Floor:String): MutableLiveData<List<Buildings>> {

        checkNetwork()
            val context = getApplication<Application>().applicationContext
            var api = Api.create(context,true)

            api.fetchAllBuildingsFilterFloor(Floor).enqueue(object: Callback<BuildingReturn> {
                override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                    Log.d("filter","good")
                    for (item in response.body()?.Buildings!!) {
                        buildingName.value?.put(
                            item.UnitID,
                            item.UnitBlock + "-" + item.UnitName + "-" + item.UnitFloor
                        )
                        recordDetails.value?.put(item.UnitID,item.LastRecordTime)
                    }
                    buildingDetails.value = response.body()?.Buildings

                    isOnline.value = true
                }

                override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                    Log.d("filter",t.message)
                    isOnline.value = false
                    api = Api.create(context,false)
                    api.fetchAllBuildingsFilterFloor(Floor).enqueue(object: Callback<BuildingReturn> {
                        override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                            Log.d("ya", response.body().toString())
                            for (item in response.body()?.Buildings!!) {
                                buildingName.value?.put(
                                    item.UnitID,
                                    item.UnitBlock + "-" + item.UnitName + "-" + item.UnitFloor
                                )
                                recordDetails.value?.put(item.UnitID,item.LastRecordTime)
                            }
                            buildingDetails.value = response.body()?.Buildings

                        }

                        override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                            Log.d("yap", t.message)
                        }

                    })

                }

            })


        return buildingDetails
    }

    fun fetchbuildingName(){
        checkNetwork()

        val context = getApplication<Application>().applicationContext
        var api = Api.create(context, true)

        api.fetchAllBuildings().enqueue(object : Callback<BuildingReturn> {
            override fun onResponse(
                call: Call<BuildingReturn>,
                response: Response<BuildingReturn>
            ) {
                Log.d("ya", response.body().toString())

                for (item in response.body()?.Buildings!!) {
                    buildingName.value?.put(
                        item.UnitID,
                        item.UnitBlock + "-" + item.UnitName + "-" + item.UnitFloor
                    )
                    recordDetails.value?.put(item.UnitID,item.LastRecordTime)
                }
                Log.d("name",buildingName.value.toString())
                Log.d("date",recordDetails.value.toString())
                buildingDetails.value = response.body()?.Buildings
                Log.d("details",buildingDetails.value.toString())
                isOnline.value = true

            }

            override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                Log.d("yap", t.message)
                isOnline.value = false
                api = Api.create(context, false)
                api.fetchAllBuildings().enqueue(object : Callback<BuildingReturn> {
                    override fun onResponse(
                        call: Call<BuildingReturn>,
                        response: Response<BuildingReturn>
                    ) {
                        Log.d("ya", response.body().toString())
                        for (item in response.body()?.Buildings!!) {
                            buildingName.value?.put(
                                item.UnitID,
                                item.UnitBlock + "-" + item.UnitName + "-" + item.UnitFloor
                            )
                            recordDetails.value?.put(item.UnitID,item.LastRecordTime)

                        }
                        buildingDetails.value = response.body()?.Buildings

                    }

                    override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                        Log.d("yap", t.message)
                    }

                })
            }
        })
    }

    fun fetchBuildingDetails(): MutableLiveData<List<Buildings>> {

        checkNetwork()

            val context = getApplication<Application>().applicationContext
            var api = Api.create(context,true)

            api.fetchAllBuildings().enqueue(object: Callback<BuildingReturn> {
                override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                    Log.d("ya", response.body().toString())
                    for (item in response.body()?.Buildings!!) {
                        buildingName.value?.put(
                            item.UnitID,
                            item.UnitBlock + "-" + item.UnitName + "-" + item.UnitFloor
                        )
                        recordDetails.value?.put(item.UnitID,item.LastRecordTime)

                    }
                    Log.d("name",buildingName.value.toString())
                    Log.d("date",recordDetails.value.toString())
                    buildingDetails.value = response.body()?.Buildings
                    Log.d("details",buildingDetails.value.toString())
                    isOnline.value = true

                }
                override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                    Log.d("yap", t.message)
                    isOnline.value = false
                    api = Api.create(context,false)
                    api.fetchAllBuildings().enqueue(object: Callback<BuildingReturn> {
                        override fun onResponse(call: Call<BuildingReturn>, response: Response<BuildingReturn>) {
                            Log.d("ya", response.body().toString())
                            for (item in response.body()?.Buildings!!) {
                                buildingName.value?.put(
                                    item.UnitID,
                                    item.UnitBlock + "-" + item.UnitName + "-" + item.UnitFloor
                                )
                                recordDetails.value?.put(item.UnitID,item.LastRecordTime)
                            }
                            buildingDetails.value = response.body()?.Buildings
                        }

                        override fun onFailure(call: Call<BuildingReturn>, t: Throwable) {
                            Log.d("yap", t.message)
                        }

                    })
                }

            })


        return buildingDetails
    }
}