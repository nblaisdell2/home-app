package com.example.homeapp.data

import com.example.homeapp.network.responses.ResponseObject
import com.example.homeapp.network.responses.ShoppingListItem
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.DELETE
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
    suspend fun getShoppingList(): ResponseObject<List<ShoppingListItem>>

    @POST("add_item")
    suspend fun addItem(itemName: String, storeName: String): Unit

    @PUT("update_item")
    suspend fun updateItem(itemID: Int, newItemName: String, newStoreName: String): Unit

    @DELETE("remove_item/{itemID}")
    suspend fun removeItem(@Path("itemID") itemID: Int): Unit
}

object ShoppingListApi {
    val retrofitService: ShoppingListApiService by lazy {
        retrofit.create(ShoppingListApiService::class.java)
    }
}