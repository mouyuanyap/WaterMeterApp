package com.example.watermeterapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.SyncStateContract
import com.example.watermeterapp.data.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface Api {

    @POST("users/authenticate")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("/buildings")
    fun fetchAllBuildings(): Call<BuildingReturn>

    @GET("/buildings/filterBlock/{id}")
    fun fetchAllBuildingsFilterBlock(@Path("id") block: String): Call<BuildingReturn>

    @GET("/buildings/filterFloor/{id}")
    fun fetchAllBuildingsFilterFloor(@Path("id") floor: String): Call<BuildingReturn>

    @GET("/buildings/filterAll/{block}/{floor}")
    fun fetchAllBuildingsFilterAll(@Path("block") block: String, @Path("floor") floor: String): Call<BuildingReturn>

    @GET("/buildings/{id}")
    fun fetchSpecificBuildings(@Path("id") id: Int): Call<RecordReturn>

    @GET("/buildings/{id}/usage")
    fun fetchSpecificUsage(@Path("id") id: Int): Call<UsageReturn>

    @GET("/buildings/{id}/usageAvg")
    fun fetchSpecificUsageAverage(@Path("id") id: Int): Call<UsageAvgReturn>

    companion object {
        fun create(context: Context, serverConn: Boolean):Api{
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

            val cacheSize = (5 * 1024 * 1024).toLong()
            val myCache = Cache(context.cacheDir, cacheSize)



            val okHttpClient = OkHttpClient.Builder()
                    .cache(myCache)
                    .addInterceptor { chain ->
                        var request = chain.request()
                        val sessionManager = SessionManager(context)
                        request = if (hasNetwork(context)!! && serverConn)

                            request.newBuilder()
                                    .addHeader("Cache-Control", "public, max-age=" + 5)
                                    .addHeader("Authorization", "Bearer "+sessionManager.fetchAuthToken())
                                    .build()
                        else
                            request.newBuilder()
                                    .addHeader("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                                    //.addHeader("Authorization", "Bearer "+sessionManager.fetchAuthToken())
                                    .build()
                        chain.proceed(request)
                    }
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .readTimeout(2, TimeUnit.SECONDS)
                    .writeTimeout(2, TimeUnit.SECONDS)
                    .build()



            val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.0.108:3000")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit.create(Api::class.java)
        }
    }

}

interface ApiPost{
    @Headers("Content-Type: application/json")
    @POST("/buildings/{id}")
    fun insertRecord(@Path("id") id: Int, @Body submit: SubmitFormat): Call<SubmitFormat>

    companion object {
        fun create(context: Context):ApiPost{

            val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .readTimeout(3, TimeUnit.SECONDS)
                    .writeTimeout(3, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        var request = chain.request()
                        val sessionManager = SessionManager(context)
                        request.newBuilder()
                                    .addHeader("Authorization", "Bearer "+sessionManager.fetchAuthToken())
                                    .build()
                        chain.proceed(request)
                    }
                    .build()

            val retrofit = Retrofit.Builder()
                    .baseUrl("http://192.168.0.108:3000")
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit.create(ApiPost::class.java)
        }
    }
}
