package se.eoslund.piggest.services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object YTApiClient {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    private var retrofit: Retrofit? = null

    val client: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}