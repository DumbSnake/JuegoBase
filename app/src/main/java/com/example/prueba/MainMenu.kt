package com.example.prueba.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenu(onPlay: () -> Unit, onShop: () -> Unit, onOptions: () -> Unit, onSkins: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6200EE))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Menú Principal", color = Color.White, fontSize = 32.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onPlay) { Text("Jugar") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onShop) { Text("Tienda") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onOptions) { Text("Opciones") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSkins) { Text("Skins") }
    }
}
