package com.example.prueba.ui

import androidx.compose.runtime.*
import com.example.prueba.R
import com.example.prueba.login.LoginScreen

// Definición de las pantallas usando una sealed class
sealed class Screen {
    object Login : Screen()
    object Menu : Screen()
    object Game : Screen()
    object Shop : Screen()
    object Options : Screen()
    object Skins : Screen()
}

@Composable
fun AppContent() {
    // Variables de estado para gestionar la navegación y datos de la app
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    var coins by remember { mutableStateOf(0) }
    var selectedSkin by remember { mutableStateOf(R.drawable.gato) }

    // Navegación entre pantallas según el valor de currentScreen
    when (currentScreen) {
        is Screen.Login -> {
            LoginScreen(onLoginSuccess = { currentScreen = Screen.Menu })
        }
        is Screen.Menu -> {
            MainMenu(
                onPlay = { currentScreen = Screen.Game },
                onShop = { currentScreen = Screen.Shop },
                onOptions = { currentScreen = Screen.Options },
                onSkins = { currentScreen = Screen.Skins }
            )
        }
        is Screen.Game -> {
            GameScreen(
                onGameOver = { earnedCoins ->
                    coins += earnedCoins
                    currentScreen = Screen.Menu
                },
                selectedSkin = selectedSkin
            )
        }
        is Screen.Shop -> {
            ShopScreen(
                coins = coins,
                onBack = { currentScreen = Screen.Menu },
                onPurchase = { cost ->
                    if (coins >= cost) coins -= cost
                }
            )
        }
        is Screen.Options -> {
            OptionsScreen(onBack = { currentScreen = Screen.Menu })
        }
        is Screen.Skins -> {
            SkinsScreen(
                selectedSkin = selectedSkin,
                onSkinSelect = { skin -> selectedSkin = skin },
                onBack = { currentScreen = Screen.Menu }
            )
        }

        else -> {}
    }
}

