package com.example.prueba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
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
    var birdY by remember { mutableStateOf(0f) }  // Posición del pájaro en el eje Y
    var velocity by remember { mutableStateOf(0f) }  // Velocidad del pájaro
    var obstacleX by remember { mutableStateOf(1000f) }  // Posición del obstáculo en X
    var isGameOver by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Ciclo de juego
        while (!isGameOver) {
            delay(16L)  // 60 FPS (aproximadamente)
            velocity += 0.5f  // Gravedad que afecta al pájaro
            birdY += velocity
            obstacleX -= 5f  // Movimiento de los obstáculos hacia la izquierda

            // Reinicia la posición del obstáculo cuando sale de la pantalla
            if (obstacleX < 0) {
                obstacleX = 1000f
            }

            // Verificar colisión del pájaro con el suelo o los obstáculos (simplificada)
            if (birdY > 1000f || birdY < 0f || (obstacleX in 450f..550f && birdY in 300f..700f)) {
                isGameOver = true
            }
        }
    }

    // Dibujar elementos del juego
     Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    // Al tocar la pantalla, el pájaro salta
                    velocity = -10f
                }
            }
    ) {
        // Dibuja el pájaro como un círculo rojo
        drawCircle(
            color = Color.Red,
            radius = 30f,
            center = Offset(x = size.width / 2, y = birdY)
        )

        // Dibuja el obstáculo como un rectángulo verde
        drawRect(
            color = Color.Green,
            topLeft = Offset(x = obstacleX, y = 300f),
            size = Size(100f, 400f)
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
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    PruebaTheme {
        GameScreen()
    }
}
