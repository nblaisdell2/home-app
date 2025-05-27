package com.example.homeapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.homeapp.network.responses.ShoppingListItem
import com.example.homeapp.network.responses.Store
import com.example.homeapp.ui.theme.HomeAppTheme

enum class ShoppingListScreen() {
    Home,
    AddEdit,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(showBack: Boolean, navigateBack: () -> Unit, shoppingListViewModel: ShoppingListViewModel, shoppingListUiState: ShoppingListUiState) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(text = "Shopping List")
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back"
                    )
                }
            }
        },
        actions = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier) {
                Text(text = "Shopping Mode")
                Switch(checked = shoppingListUiState.shoppingMode, onCheckedChange = {
                    shoppingListViewModel.toggleShoppingMode()
                })
            }
        },
        scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    windowSize: WindowWidthSizeClass,
    shoppingListViewModel: ShoppingListViewModel = viewModel(factory = ShoppingListViewModel.Factory),
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val shoppingListUiState by shoppingListViewModel.uiState.collectAsState()

    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                showBack = backStackEntry?.destination?.route != ShoppingListScreen.Home.name,
                navigateBack = { navController.popBackStack() },
                shoppingListViewModel = shoppingListViewModel,
                shoppingListUiState = shoppingListUiState
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(ShoppingListScreen.AddEdit.name) }, shape = CircleShape) {
                Icon(Icons.Filled.Add, "Add new item")
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize())
    { pad ->
        AppScreen(
            shoppingListViewModel = shoppingListViewModel,
            shoppingListUiState = shoppingListUiState,
            navController = navController,
            modifier = Modifier
                .padding(pad)
                .consumeWindowInsets(pad)
        )
    }
}

@Composable
fun AppScreen(
    shoppingListViewModel: ShoppingListViewModel,
    shoppingListUiState: ShoppingListUiState,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Log.d("ShoppingListScreens", shoppingListUiState.shoppingList.toString())

    NavHost(
        navController = navController,
        startDestination = ShoppingListScreen.Home.name,
        modifier = Modifier
    ) {
        composable(route = ShoppingListScreen.Home.name) {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(shoppingListUiState.shoppingList) { it ->
                    ListItem(it, shoppingListViewModel, shoppingListUiState)
                }
            }
        }
        composable(route = ShoppingListScreen.AddEdit.name) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier) {
                Text(text = "Add/Edit Screen")
                TextField(value = "", onValueChange = {}, placeholder = { Text(text = "Enter text here") })
            }
        }
    }
}

@Composable
fun ListItem(item: ShoppingListItem,
             shoppingListViewModel: ShoppingListViewModel,
             shoppingListUiState: ShoppingListUiState)
{
    Row(modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(0.9f)) {
            Text(text = item.name, fontSize = 16.sp, modifier = Modifier)
            Text(text = item.store.name, fontSize = 12.sp, fontStyle = FontStyle.Italic, color = Color.Gray, modifier = Modifier)
        }
        IconButton(onClick = { shoppingListViewModel.deleteItem(item.id) }) {
            if (shoppingListUiState.shoppingMode) {
                Icon(Icons.Filled.Check, "Delete item", tint = Color(0.0f, 0.7f, 0.0f, 1.0f), modifier = Modifier.weight(0.1f))
            } else {
                Icon(Icons.Filled.Delete, "Delete item", tint = Color(0.8f, 0.0f, 0.0f, 1.0f), modifier = Modifier.weight(0.1f))
            }
        }
    }
    HorizontalDivider(thickness = 1.dp, color = Color.LightGray, modifier = Modifier)
}

//@Preview(showBackground = true)
//@Composable
//fun ListItemPreview() {
//    ListItem(ShoppingListItem(1, "New Computer", Store(id =  1, name = "Walmart")), false)
//}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun GreetingPreview() {
//    HomeAppTheme {
//        App(windowSize = WindowWidthSizeClass.Medium)
//    }
//}
