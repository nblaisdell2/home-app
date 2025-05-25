package com.example.homeapp

import com.example.homeapp.data.NetworkShoppingListRepository
import com.example.homeapp.data.ShoppingListApi
import com.example.homeapp.data.ShoppingListRepository

interface AppContainer {
    val shoppingListRepository: ShoppingListRepository
}

class DefaultAppContainer: AppContainer {
    override val shoppingListRepository: ShoppingListRepository by lazy {
        NetworkShoppingListRepository(ShoppingListApi.retrofitService)
    }
}