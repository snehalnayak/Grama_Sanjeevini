package com.example.grama_sanjeevini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.grama_sanjeevini.ui.navigation.AppNavigation
import com.example.grama_sanjeevini.ui.theme.Grama_SanjeeviniTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Grama_SanjeeviniTheme {
                AppNavigation()
            }
        }
    }
}
