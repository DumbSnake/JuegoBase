package com.example.prueba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prueba.ui.theme.PruebaTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PruebaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    // Variables del juego
    var catY by remember { mutableStateOf(0f) }  // Posición del pájaro en el eje Y
    var velocity by remember { mutableStateOf(0f) }  // Velocidad del pájaro
    var obstacleX by remember { mutableStateOf(1000f) }  // Posición del obstáculo en X
    var isGameOver by remember { mutableStateOf(false) }

    // Cargar imágenes
    val catImage = ImageBitmap.imageResource(id = R.drawable.boy)
    val obstacleImage = ImageBitmap.imageResource(id = R.drawable.edificio)

    // Dependiendo de `isGameOver`, reinicia el ciclo del juego cuando se cambia a `false`
    LaunchedEffect(isGameOver) {
        // Solo ejecuta el ciclo si el juego no ha terminado
        while (!isGameOver) {
            delay(16L)  // 60 FPS (aproximadamente)
            velocity += 0.5f  // Gravedad que afecta al pájaro
            catY += velocity
            obstacleX -= 5f  // Movimiento de los obstáculos hacia la izquierda

            // Reinicia la posición del obstáculo cuando sale de la pantalla
            if (obstacleX < 0) {
                obstacleX = 1000f
            }

            // Verificar colisión del pájaro con el suelo o los obstáculos (simplificada)
            if (catY > 1000f || catY < 0f || (obstacleX in 450f..550f && catY in 300f..700f)) {
                isGameOver = true
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Dibujar elementos del juego
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        // Al tocar la pantalla, el pájaro salta
                        velocity = -10f
                    }
                }
        ) {
            // Dibuja el pájaro usando la imagen en lugar del círculo rojo
            drawImage(
                image = catImage,
                topLeft = Offset(x = size.width / 2 - catImage.width / 2, y = catY - catImage.height / 2)
            )

            // Dibuja el obstáculo usando la imagen en lugar del rectángulo verde
            drawImage(
                image = obstacleImage,
                topLeft = Offset(x = obstacleX, y = 300f)
            )

            // Si el juego termina, dibuja el mensaje de "Game Over"
            if (isGameOver) {
                drawContext.canvas.nativeCanvas.drawText(
                    "Game Over",
                    size.width / 2,
                    size.height / 2,
                    android.graphics.Paint().apply {
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 64f
                        color = android.graphics.Color.RED
                    }
                )
            }
        }

        // Botón de reintentar, que aparece solo cuando el juego ha terminado
        if (isGameOver) {
            Button(
                onClick = {
                    // Reinicia las variables del juego
                    catY = 0f
                    velocity = 0f
                    obstacleX = 1000f
                    isGameOver = false
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 100.dp)
            ) {
                Text("Reintentar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    PruebaTheme {
        GameScreen()
    }
}
