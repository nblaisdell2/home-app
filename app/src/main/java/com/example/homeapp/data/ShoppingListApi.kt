package com.example.homeapp.data

import com.example.homeapp.network.responses.ResponseObject
import com.example.homeapp.network.responses.ShoppingListData
import com.example.homeapp.network.responses.ShoppingListItem
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

private const val BASE_URL = "https://40xeipz4ec.execute-api.us-east-1.amazonaws.com/dev/";

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface ShoppingListApiService {
    @GET("get_shopping_list")
    suspend fun getShoppingList(): ResponseObject<ShoppingListData>

    @FormUrlEncoded
    @POST("add_item")
    suspend fun addItem(@Field("itemName") itemName: String, @Field("storeName") storeName: String): Unit

    @FormUrlEncoded
    @PUT("update_item")
    suspend fun updateItem(@Field("itemID") itemID: Int, @Field("newItemName") newItemName: String, @Field("newStoreName") newStoreName: String): Unit

    @DELETE("remove_item/{itemID}")
    suspend fun removeItem(@Path("itemID") itemID: Int): Unit
}

object ShoppingListApi {
    val retrofitService: ShoppingListApiService by lazy {
        retrofit.create(ShoppingListApiService::class.java)
    }
}