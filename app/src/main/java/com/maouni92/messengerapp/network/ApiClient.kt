package com.maouni92.messengerapp.network

import com.maouni92.messengerapp.helper.Constants
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


interface ApiClient {

    companion object{
        private var retrofit: Retrofit? = null

        val getClient : Retrofit get(){
            if (retrofit == null){
                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.FCM_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }
    }
}