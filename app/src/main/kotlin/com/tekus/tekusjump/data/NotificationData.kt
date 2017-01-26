package com.tekus.tekusjump.data

import android.util.Log
import com.google.gson.GsonBuilder
import com.tekus.tekusjump.model.Notification
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

/**
 * Created by Jose Daniel on 21/01/2017.
 */
class NotificationData {

    val BASE_URL: String = "http://proyectos.tekus.co/Test/api/"
    var retrofit: Retrofit
    val apiService: ApiInterface

    init {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
        retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).build()

        apiService = retrofit.create(ApiInterface::class.java)
    }

    interface ApiInterface {
        @Headers("Authorization: Basic 1152197056")
        @GET("notifications/{id}")
        fun getNotification(@Path("id") id: Int): Call<Notification>

        @Headers("Authorization: Basic 1152197056")
        @GET("notifications/")
        fun getNotifications(): Call<ArrayList<Notification>>

        @Headers("Authorization: Basic 1152197056")
        @POST("notifications/")
        fun createNotification(@Body notification: Notification): Call<Notification>

        @Headers("Authorization: Basic 1152197056")
        @PUT("notifications/{id}")
        fun updateNotification(@Path("id") id: Int, @Body notification: Notification): Call<Notification>

        @Headers("Authorization: Basic 1152197056")
        @DELETE("notifications/{id}")
        fun deleteNotification(@Path("id") id: Int): Call<ResponseBody>
    }

    fun getNotification(id: Int, callback: (Notification?) -> Unit) {
        val call = apiService.getNotification(id)
        call.enqueue(object : Callback<Notification> {
            override fun onResponse(call: Call<Notification>, response: Response<Notification>) {
                Log.i("responseCode", response.code().toString())
                if (response.isSuccessful) callback(response.body())
            }

            override fun onFailure(call: Call<Notification>, t: Throwable) {
                t.printStackTrace()
                callback(null)
            }
        })
    }

    fun getNotifications(callback: (ArrayList<Notification>?) -> Unit) {
        val call = apiService.getNotifications()
        call.enqueue(object : Callback<ArrayList<Notification>> {
            override fun onResponse(call: Call<ArrayList<Notification>>, response: Response<ArrayList<Notification>>) {
                Log.i("responseCode", response.code().toString())
                if (response.isSuccessful) callback(response.body())
            }

            override fun onFailure(call: Call<ArrayList<Notification>>, t: Throwable) {
                t.printStackTrace()
                callback(null)
            }
        })
    }

    fun createNotifications(notification: Notification, callback: (Notification?) -> Unit) {
        val call = apiService.createNotification(notification)
        call.enqueue(object : Callback<Notification> {
            override fun onResponse(call: Call<Notification>, response: Response<Notification>) {
                Log.i("responseCode", response.code().toString())
                if (response.isSuccessful) callback(response.body())
            }

            override fun onFailure(call: Call<Notification>, t: Throwable) {
                t.printStackTrace()
                callback(null)
            }
        })
    }

    fun updateNotifications(id: Int, notification: Notification, callback: (Notification?) -> Unit) {
        val call = apiService.updateNotification(id, notification)
        call.enqueue(object : Callback<Notification> {
            override fun onResponse(call: Call<Notification>, response: Response<Notification>) {
                Log.i("responseCode", response.code().toString())
                if (response.isSuccessful) callback(response.body())
            }

            override fun onFailure(call: Call<Notification>, t: Throwable) {
                t.printStackTrace()
                callback(null)
            }
        })
    }

    fun deleteNotifications(id: Int, callback: () -> Unit) {
        val call = apiService.deleteNotification(id)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("responseCode", response.code().toString())
                if (response.isSuccessful) callback()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                callback()
            }
        })
    }
}