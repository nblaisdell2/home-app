package com.example.homeapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun App(
    windowSize: WindowWidthSizeClass,
    shoppingListViewModel: ShoppingListViewModel = viewModel(factory = ShoppingListViewModel.Factory),
    modifier: Modifier = Modifier
) {
    val shoppingListUiState by shoppingListViewModel.uiState.collectAsState()

    Scaffold(topBar = {
        Text(text = "Hello")
    }, modifier = Modifier.fillMaxSize()) { innerPadding ->
        AppScreen(
            shoppingListViewModel = shoppingListViewModel,
            shoppingListUiState = shoppingListUiState,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AppScreen(
    shoppingListViewModel: ShoppingListViewModel,
    shoppingListUiState: ShoppingListUiState,
    modifier: Modifier = Modifier
) {
    Log.d("ShoppingListScreens", shoppingListUiState.shoppingList.toString())

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier)
    {
        Text(text = "Shopping List")
    }
}
