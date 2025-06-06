package com.example.homeapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.homeapp.HomeApplication
import com.example.homeapp.data.ShoppingListApi
import com.example.homeapp.data.ShoppingListRepository
import com.example.homeapp.network.responses.ShoppingListItem
import com.example.homeapp.network.responses.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.io.IOException

//sealed interface ShoppingListUiState<out T> {
//    data class Success<T>(val photos: T) : ShoppingListUiState<T>
//    object Error : ShoppingListUiState<Nothing>
//    object Loading : ShoppingListUiState<Nothing>
//}

data class ShoppingListUiState(
    val shoppingList: List<ShoppingListItem> = listOf(),
    val storeList: List<String> = listOf(),
//    val shoppingMode: Boolean = false
)

class ShoppingListViewModel(private val shoppingListRepository: ShoppingListRepository): ViewModel() {
//    var shoppingListUiState: ShoppingListUiState<Nothing> by mutableStateOf(ShoppingListUiState.Loading)
//        private set
//    var shoppingList: List<ShoppingListItem> by mutableStateOf(listOf())
    private val _uiState = MutableStateFlow(ShoppingListUiState(listOf()))
    val uiState: StateFlow<ShoppingListUiState> = _uiState.asStateFlow()


    init {
        getShoppingList()
    }

    fun getShoppingList() {
        viewModelScope.launch {
            _uiState.update { it ->
                val data = shoppingListRepository.getShoppingList().data
                val shoppingList = data.items
                val storeList = data.stores.map { it.name }
                it.copy(shoppingList = shoppingList.toMutableList(), storeList = storeList)
            }
//            shoppingListUiState = try {
//                val shoppingList = ShoppingListApi.retrofitService.getShoppingList()
//                ShoppingListUiState<List<ShoppingListItem>>.Success(shoppingList);
//            } catch (e: IOException) {
//                ShoppingListUiState.Error
//            }
        }
    }

//    fun toggleShoppingMode() {
//        _uiState.update { it ->
//            it.copy(shoppingMode = !it.shoppingMode)
//        }
//    }

    fun deleteItem(itemID: Int) {
        viewModelScope.launch {
            shoppingListRepository.removeItem(itemID)
            _uiState.update { it ->
                val newList = it.shoppingList.filter { it ->
                    it.id != itemID
                }
                it.copy(shoppingList = newList)
            }
        }
    }

    fun updateItem(itemID: Int, newItemName: String, newStoreName: String) {
        viewModelScope.launch {
            shoppingListRepository.updateItem(itemID, newItemName, newStoreName)
            _uiState.update { it ->
                val newList = it.shoppingList.map {
                    if (it.id != itemID) { it }
                    else { it.copy(it.id, newItemName, it.store.copy(it.store.id, newStoreName)) }
                }
                var currStoreList = it.storeList.toMutableList()
                if (!it.storeList.contains(newStoreName)) {
                    currStoreList.add(newStoreName)
                }
                it.copy(shoppingList = newList, storeList = currStoreList)
            }
        }
    }

    fun addItem(itemName: String, storeName: String) {
        viewModelScope.launch {
            shoppingListRepository.addItem(itemName, storeName)
            _uiState.update { it ->
                var newList = it.shoppingList.toMutableList()
                var currStoreList = it.storeList.toMutableList()

                val nextItemID = newList.maxBy { it -> it.id }.id + 1
                val storeID = getStoreID(storeName)
                newList.add(ShoppingListItem(nextItemID, itemName, Store(storeID, storeName)))
                if (!it.storeList.contains(storeName)) {
                    currStoreList.add(storeName)
                }
                it.copy(shoppingList = newList, storeList = currStoreList)
            }
        }
    }

    private fun getStoreID(storeName: String): Int {
        val currList = _uiState.asStateFlow().value.shoppingList
        val found = currList.firstOrNull {
            it.store.name == storeName
        }

        return found?.store?.id ?: (currList.distinctBy { it.store.id }.last().id + 1)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HomeApplication)
                val shoppingListRepository = application.container.shoppingListRepository
                ShoppingListViewModel(shoppingListRepository = shoppingListRepository)
            }
        }
    }
}