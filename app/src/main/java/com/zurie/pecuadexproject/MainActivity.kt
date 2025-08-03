package com.zurie.pecuadexproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import androidx.navigation.compose.rememberNavController
import com.zurie.pecuadexproject.View.AppNavGraph
import com.zurie.pecuadexproject.ViewModels.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            val navController = rememberNavController()
            val viewModel: MainViewModel = viewModel()

            AppNavGraph(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}