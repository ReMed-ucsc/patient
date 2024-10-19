package com.example.remed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.remed.navigation.graph.RootNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()

//        val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
//                RootNavGraph(loginViewModel)
                RootNavGraph()
            }
        }
    }
}