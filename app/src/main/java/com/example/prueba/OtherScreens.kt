package com.example.prueba.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prueba.R

@Composable
fun OptionsScreen(onBack: () -> Unit) {
    var volume by remember { mutableStateOf(0.5f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Opciones", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Volumen: ${"%.0f".format(volume * 100)}%", fontSize = 18.sp)
        Slider(
            value = volume,
            onValueChange = { volume = it },
            valueRange = 0f..1f,
            steps = 10,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Regresar") }
    }
}

@Composable
fun SkinsScreen(selectedSkin: Int, onSkinSelect: (Int) -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Seleccionar Skin", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onSkinSelect(R.drawable.boy) }) { Text("Skin 1") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onSkinSelect(R.drawable.gato) }) { Text("Skin 2") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Regresar") }
    }
}

@Composable
fun ShopScreen(coins: Int, onBack: () -> Unit, onPurchase: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Tienda", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Monedas disponibles: $coins", fontSize = 18.sp, color = androidx.compose.ui.graphics.Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onPurchase(50) }, enabled = coins >= 50) { Text("Comprar Skin 1 - 50 monedas") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onPurchase(100) }, enabled = coins >= 100) { Text("Comprar Skin 2 - 100 monedas") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) { Text("Regresar") }
    }
}
