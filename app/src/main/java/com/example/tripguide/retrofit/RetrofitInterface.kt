package com.example.tripguide.retrofit

import com.example.tripguide.kakao.KakaoData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("v2/local/search/address.json")
    fun getKakaoAddress(
        @Header("Aythorization") key: String,
        @Query("query") query: String?
    ): Call<KakaoData>
}

