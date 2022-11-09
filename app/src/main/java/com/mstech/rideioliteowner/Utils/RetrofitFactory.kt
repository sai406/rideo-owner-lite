package com.mstech.rideiodriver.Utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitFactory {
    private var retrofit: Retrofit? = null
    // change your base URL
    //Creating object for our interface
    val client: ApiInterface // return the APIInterface object
        get() { // change your base URL
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl("http://www.launchapps.com.au/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            //Creating object for our interface
            return retrofit!!.create<ApiInterface>(
                ApiInterface::class.java) // return the APIInterface object
        }
}
