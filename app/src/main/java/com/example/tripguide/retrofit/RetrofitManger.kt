package com.example.tripguide.retrofit

import android.util.Log
import com.example.tripguide.model.Region
import com.example.tripguide.utils.Constants
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitManger {

    private val client_id = "tlsWuqVpcy0CY6oqvnhf"
    private val client_pw = "IRVqBATb7R"

    fun getResultSearch() {
        val retrofitInterface: RetrofitInterface? = RetrofitClient.getInstance()?.create(RetrofitInterface::class.java)
        val call: Call<JsonElement> = retrofitInterface?.getSearchResult(client_id, client_pw, query = null).let {
            it
        }?: return

        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    Log.d(Constants.TAG, "성공 : $result")
                    response.body()?.let {
                        var parseRegionDataArray = ArrayList<Region>()
                        val body = it.asJsonObject
                        val results = body.getAsJsonArray("results")
                        val total = body.get("total").asInt

                        if (total == 0) {
                        }
                        else {
                            results.forEach { resultItem ->
                                val resultItemObject = resultItem.asJsonObject

                            }
                        }

                    }
                } else {
                    Log.d(Constants.TAG, "실패 : " + response.body())
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.d(Constants.TAG, "에러 : " + t.message)
            }
        })
    }

}