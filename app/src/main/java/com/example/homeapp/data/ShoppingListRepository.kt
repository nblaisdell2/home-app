package com.example.homeapp.data

import com.example.homeapp.network.responses.ResponseObject
import com.example.homeapp.network.responses.ShoppingListItem
import retrofit2.Call

interface ShoppingListRepository {
    suspend fun getShoppingList(): ResponseObject<List<ShoppingListItem>>
    suspend fun addItem(itemName: String, storeName: String): Unit
    suspend fun updateItem(itemID: Int, newItemName: String, newStoreName: String): Unit
    suspend fun removeItem(itemID: Int): Unit
}

class NetworkShoppingListRepository(private val shoppingListApiService: ShoppingListApiService): ShoppingListRepository {
    override suspend fun getShoppingList(): ResponseObject<List<ShoppingListItem>> = shoppingListApiService.getShoppingList()
    override suspend fun addItem(itemName: String, storeName: String): Unit = shoppingListApiService.addItem(itemName, storeName)
    override suspend fun updateItem(itemID: Int, newItemName: String, newStoreName: String): Unit = shoppingListApiService.updateItem(itemID, newItemName, newStoreName)
    override suspend fun removeItem(itemID: Int): Unit = shoppingListApiService.removeItem(itemID)
}