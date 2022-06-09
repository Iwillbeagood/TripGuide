package com.example.tripguide.retrofit

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.tripguide.App
import com.example.tripguide.kakao.KakaoData
import com.example.tripguide.utils.API
import com.example.tripguide.utils.Constants.TAG
import com.example.tripguide.utils.KakaoApi
import com.example.tripguide.utils.isJsonArray
import com.example.tripguide.utils.isJsonobject
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private val retrofit: Retrofit.Builder by lazy {

    }
}