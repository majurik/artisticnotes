package com.example.artisticnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.artisticnotes.ui.*
import com.example.artisticnotes.ui.theme.ArtisticNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as ArtisticNotesApp
        setContent {
            ArtisticNotesTheme {
                ArtisticNotesNav(app)
            }
        }
    }
}

@Composable
fun ArtisticNotesNav(app: ArtisticNotesApp) {
    val navController = rememberNavController()
    val viewModel: MediaViewModel = viewModel(
        factory = MediaViewModel.factory(app.repository)
    )

    NavHost(
        navController = navController,
        startDestination = "catalog"
    ) {
        composable("catalog") {
            CatalogScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate("edit") },
                onItemClick = { id -> navController.navigate("detail/$id") }
            )
        }
        composable("edit") {
            EditScreen(
                viewModel = viewModel,
                itemId = null,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("id")
            EditScreen(
                viewModel = viewModel,
                itemId = id,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("id") ?: 0L
            DetailScreen(
                viewModel = viewModel,
                itemId = id,
                onBack = { navController.popBackStack() },
                onEdit = { editId -> navController.navigate("edit/$editId") }
            )
        }
    }
}
