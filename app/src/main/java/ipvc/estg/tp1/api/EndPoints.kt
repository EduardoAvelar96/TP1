package ipvc.estg.tp1.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {
    @GET("/myslim/api/problems")
    fun getUsers(): Call<List<User>>

    @GET("/myslim/api/filtrar/{id}")
    fun filtrar(@Path("id")id:Int): Call<List<User>>

    @FormUrlEncoded
    @POST("/myslim/api/post")
    fun postTest(@Field("id") ID: Int,@Field("type") type: Int,@Field("lat") lat: String, @Field("lng") lng: String): Call<OutputPost>
}