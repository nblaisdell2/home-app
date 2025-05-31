package com.example.homeapp.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.homeapp.network.responses.ShoppingListItem
import kotlin.math.exp

enum class ShoppingListScreen() {
    Home,
    AddEdit,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, showBack: Boolean, navigateBack: () -> Unit, shoppingListViewModel: ShoppingListViewModel, shoppingListUiState: ShoppingListUiState) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(text = title)
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
//        actions = {
//            if (!showBack) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier) {
//                    Text(text = "Shopping Mode")
//                    Switch(checked = shoppingListUiState.shoppingMode, onCheckedChange = {
//                        shoppingListViewModel.toggleShoppingMode()
//                    })
//                }
//            }
//        },
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
    var title by remember {
        mutableStateOf("Shopping List")
    }

    val backStackEntry by navController.currentBackStackEntryAsState()

    var itemID by remember {
        mutableStateOf(0)
    }

    var itemName by remember {
        mutableStateOf("")
    }

    var storeName by remember {
        mutableStateOf("")
    }

    val showBack = backStackEntry?.destination?.route != ShoppingListScreen.Home.name

    Scaffold(
        topBar = {
            AppTopBar(
                title = title,
                showBack = showBack,
                navigateBack = { title = "Shopping List"; itemName = ""; storeName = ""; navController.popBackStack() },
                shoppingListViewModel = shoppingListViewModel,
                shoppingListUiState = shoppingListUiState
            )
        },
        floatingActionButton = {
            if (!showBack) {
                FloatingActionButton(onClick = {
                    title = "Add Item"
                    navController.navigate(ShoppingListScreen.AddEdit.name)
                }, shape = CircleShape) {
                    Icon(Icons.Filled.Add, "Add new item")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay,
        modifier = Modifier.fillMaxSize())
    { pad ->
        AppScreen(
            shoppingListViewModel = shoppingListViewModel,
            shoppingListUiState = shoppingListUiState,
            navController = navController,
            itemID = itemID,
            itemName = itemName,
            storeName = storeName,
            title = title,
            updateTitle = {
                title = it
            },
            updateItemID = {
                itemID = it
            },
            updateItemName = {
                itemName = it
            },
            updateStoreName = {
                storeName = it
            },
            openEditScreen = { currItemID, currItemName, currStoreName ->
                itemID = currItemID
                itemName = currItemName
                storeName = currStoreName
                title = "Edit Item"
                navController.navigate(ShoppingListScreen.AddEdit.name)
            },
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
    itemID: Int,
    itemName: String,
    storeName: String,
    title: String,
    updateTitle: (String) -> Unit,
    updateItemID: (Int) -> Unit,
    updateItemName: (String) -> Unit,
    updateStoreName: (String) -> Unit,
    openEditScreen: (Int, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("ShoppingListScreens", shoppingListUiState.shoppingList.toString())
    val storeList = shoppingListUiState.storeList

    NavHost(
        navController = navController,
        startDestination = ShoppingListScreen.Home.name,
        modifier = Modifier
    ) {
        composable(route = ShoppingListScreen.Home.name) {
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(shoppingListUiState.shoppingList) { it ->
                    ListItem(it, shoppingListViewModel, shoppingListUiState, openEditScreen)
                }
            }
        }
        composable(route = ShoppingListScreen.AddEdit.name) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
                Text(text = "Item")
                TextField(value = itemName, onValueChange = { updateItemName(it) }, placeholder = { Text(text = "Item Name") })

                Spacer(modifier = Modifier.height(32.dp))

                AutoComplete(storeList, storeName, updateStoreName = { updateStoreName(it) })

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (title == "Edit Item") {
                            shoppingListViewModel.updateItem(itemID, itemName, storeName)
                        } else {
                            shoppingListViewModel.addItem(itemName, storeName)
                        }
                        updateTitle("Shopping List")
                        updateItemID(0)
                        updateItemName("")
                        updateStoreName("")
                        navController.popBackStack()
                    },
                    modifier = Modifier
                ) {
                    Text(text = title)
                }
            }
        }
    }
}

@Composable
fun AutoComplete(storeList: List<String>, storeName: String, updateStoreName: (String) -> Unit, modifier: Modifier = Modifier) {
    val heightTextFields by remember {
        mutableStateOf(55.dp)
    }

    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }

    var expanded by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }

    // Category Field
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    expanded = false
                }
            )
    ) {
        Text(
            modifier = Modifier.padding(start = 3.dp, bottom = 2.dp),
            text = "Category",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heightTextFields)
                        .border(
                            width = 1.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(15.dp)
                        )
                        .onGloballyPositioned { coords ->
                            textFieldSize = coords.size.toSize()
                        }
                        .onFocusChanged {
                            expanded = it.isFocused
                        },
                    value = storeName,
                    onValueChange = {
                        updateStoreName(it)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text,
                    ),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = "arrow"
                            )
                        }
                    }
                )
            }

            AnimatedVisibility(visible = expanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFieldSize.width.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 150.dp),
                    ) {
                        if (storeName.isNotEmpty()) {
                            items(
                                storeList.filter {
                                    it.lowercase().contains(storeName.lowercase())
                                }.sorted()
                            ) {
                                CategoryItems(title = it) { title ->
                                    updateStoreName(title)
                                    expanded = false
                                }
                            }
                        } else {
                            items(storeList.sorted()) {
                                CategoryItems(title = it) { title ->
                                    updateStoreName(title)
                                    expanded = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItems(title: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onSelect(title)
            }
    ) {
        Text(text = title, fontSize = 16.sp)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListItem(item: ShoppingListItem,
             shoppingListViewModel: ShoppingListViewModel,
             shoppingListUiState: ShoppingListUiState,
             openEditScreen: (Int, String, String) -> Unit)
{
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Row(modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(0.9f).combinedClickable(interactionSource = interactionSource, indication = null, onClick = {}, onLongClick = {
            openEditScreen(item.id, item.name, item.store.name)
        })) {
            Text(text = item.name, fontSize = 16.sp, modifier = Modifier)
            Text(text = item.store.name, fontSize = 12.sp, fontStyle = FontStyle.Italic, color = Color.Gray, modifier = Modifier)
        }
        IconButton(onClick = { shoppingListViewModel.deleteItem(item.id) }) {
//            if (shoppingListUiState.shoppingMode) {
//                Icon(Icons.Filled.Check, "Delete item", tint = Color(0.0f, 0.7f, 0.0f, 1.0f), modifier = Modifier.weight(0.1f))
//            } else {
//                Icon(Icons.Filled.Delete, "Delete item", tint = Color(0.8f, 0.0f, 0.0f, 1.0f), modifier = Modifier.weight(0.1f))
//            }
            Icon(Icons.Filled.Delete, "Delete item", tint = Color(0.8f, 0.0f, 0.0f, 1.0f), modifier = Modifier.weight(0.1f))
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
