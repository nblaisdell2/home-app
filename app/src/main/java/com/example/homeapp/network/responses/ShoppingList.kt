package com.example.homeapp.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class ResponseObject<out T>(
    val data: T,
    val err: String,
    val message: String,
)

@Serializable
data class ShoppingListItem(
    val id: Int,
    val name: String,
    val store: Store
)

@Serializable
data class Store(
    val id: Int,
    val name: String
)