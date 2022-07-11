package se.eoslund.piggest.services

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import se.eoslund.piggest.model.SearchListData

interface YTApiInterface {

    //https://www.googleapis.com/youtube/v3/search?key=AIzaSyBxQ4uUEYTfTBp72F4EJzYueGLZa3v7Kmc&channelId=UCfoQAv5xCoEEEutwU998--w&part=snippet,id&order=date&maxResults=50

    @GET("search?")
    fun getList(
        @Query("key") apiKey: String,
        @Query("channelId") channelId: String,
        @Query("part") part: String,
        @Query("order") order: String,
        @Query("maxResults") limit: Int
    ): Call<SearchListData>
}