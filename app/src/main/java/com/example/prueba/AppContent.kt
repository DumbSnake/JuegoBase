package com.example.prueba.ui

import androidx.compose.runtime.*
import com.example.prueba.R

@Composable
fun AppContent() {
    var currentScreen by remember { mutableStateOf("menu") }
    var coins by remember { mutableStateOf(0) }
    var selectedSkin by remember { mutableStateOf(R.drawable.gato) }

    when (currentScreen) {
        "menu" -> MainMenu(
            onPlay = { currentScreen = "game" },
            onShop = { currentScreen = "shop" },
            onOptions = { currentScreen = "options" },
            onSkins = { currentScreen = "skins" }
        )
        "game" -> GameScreen(
            onGameOver = { earnedCoins ->
                coins += earnedCoins
                currentScreen = "menu"
            },
            selectedSkin = selectedSkin
        )
        "shop" -> ShopScreen(
            coins = coins,
            onBack = { currentScreen = "menu" },
            onPurchase = { cost ->
                if (coins >= cost) coins -= cost
            }
        )
        "options" -> OptionsScreen(onBack = { currentScreen = "menu" })
        "skins" -> SkinsScreen(
            selectedSkin = selectedSkin,
            onSkinSelect = { skin -> selectedSkin = skin },
            onBack = { currentScreen = "menu" }
        )
    }
}
