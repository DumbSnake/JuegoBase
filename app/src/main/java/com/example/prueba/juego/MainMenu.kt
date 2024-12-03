package com.example.prueba.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenu(
    onPlay: () -> Unit,
    onShop: () -> Unit,
    onOptions: () -> Unit,
    onSkins: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                )
            ))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Menú Principal",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onPlay, modifier = Modifier.fillMaxWidth(0.8f)) {
                Text("Jugar")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onShop, modifier = Modifier.fillMaxWidth(0.8f)) {
                Text("Tienda")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onOptions, modifier = Modifier.fillMaxWidth(0.8f)) {
                Text("Opciones")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onSkins, modifier = Modifier.fillMaxWidth(0.8f)) {
                Text("Skins")
            }
        }
    }
}
