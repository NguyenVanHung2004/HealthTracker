package com.example.healthtracker.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.healthtracker.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthtracker.presentation.activity.ActivityScreen
import com.example.healthtracker.presentation.activity.AddExerciseScreen
import com.example.healthtracker.presentation.settings.SettingsScreen
import com.example.healthtracker.presentation.meal.MealScreen

@Composable
fun MainScreen(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf(
        Pair(R.string.tab_dashboard, Icons.Filled.Home),
        Pair(R.string.tab_meal, Icons.Filled.RestaurantMenu),
        Pair(R.string.tab_activity, Icons.AutoMirrored.Filled.List),
        Pair(R.string.tab_setting, Icons.Filled.Settings)
    )

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600

    if (isTablet) {
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                items.forEachIndexed { index, item ->
                    NavigationRailItem(
                        icon = { Icon(item.second, contentDescription = stringResource(item.first)) },
                        label = { Text(stringResource(item.first)) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (selectedItem == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Dashboard")
                    }
                }
                if (selectedItem == 1) {
                    MealScreen()
                }
                if (selectedItem == 2) {
                    val activityNavController = rememberNavController()
                    NavHost(navController = activityNavController, startDestination = "activity_list") {
                        composable("activity_list") {
                            com.example.healthtracker.presentation.activity.ActivityScreen(
                                onNavigateToAdd = { activityNavController.navigate("add_exercise") }
                            )
                        }
                        composable("add_exercise") {
                            AddExerciseScreen(
                                onNavigateBack = { activityNavController.popBackStack() }
                            )
                        }
                    }
                }
                if (selectedItem == 3) {
                    SettingsScreen()
                }
            }
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.second, contentDescription = stringResource(item.first)) },
                            label = { Text(stringResource(item.first)) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                if (selectedItem == 0) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Dashboard")
                    }
                }
                if (selectedItem == 1) {
                    MealScreen()
                }
                if (selectedItem == 2) {
                    val activityNavController = rememberNavController()
                    NavHost(navController = activityNavController, startDestination = "activity_list") {
                        composable("activity_list") {
                            ActivityScreen(
                                onNavigateToAdd = { activityNavController.navigate("add_exercise") }
                            )
                        }
                        composable("add_exercise") {
                            AddExerciseScreen(
                                onNavigateBack = { activityNavController.popBackStack() }
                            )
                        }
                    }
                }
                if (selectedItem == 3) {
                    SettingsScreen()
                }
            }
        }
    }
}
