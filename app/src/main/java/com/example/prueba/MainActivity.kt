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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val context = LocalContext.current
    val db = remember { GameDatabase(context) }

    // Variables del juego
    var birdY by remember { mutableStateOf(0f) }
    var birdX by remember { mutableStateOf(0f) }
    var velocity by remember { mutableStateOf(0f) }
    var obstacleX by remember { mutableStateOf(1000f) }
    var obstacleY by remember { mutableStateOf(0f) }
    var isGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    val birdImage = ImageBitmap.imageResource(id = R.drawable.boy)
    val obstacleImage = ImageBitmap.imageResource(id = R.drawable.pngegg)

    // Puntajes altos inicializados
    var highScores by remember { mutableStateOf(listOf<Int>()) }

    // Dimensiones ajustadas del pájaro y del obstáculo
    val birdWidth = birdImage.width * 0.5f
    val birdHeight = birdImage.height * 0.5f
    var obstacleWidth = obstacleImage.width * 0.7f
    var obstacleHeight = obstacleImage.height * 0.7f

    // Cargar puntajes al iniciar el juego
    LaunchedEffect(Unit) {
        highScores = db.getTopScores()  // Obtener los 5 puntajes más altos
    }

    LaunchedEffect(isGameOver) {
        while (!isGameOver) {
            delay(16L)
            velocity += 0.5f
            birdY += velocity
            obstacleX -= 5f
            score += 1

            // Reiniciar obstáculo cuando sale de la pantalla
            if (obstacleX < -obstacleWidth) {
                obstacleX = 1000f

                // Alterna entre obstáculo superior e inferior y aleatoriza su tamaño
                if (Math.random() < 0.5) {
                    obstacleY = 0f  // Obstáculo en la parte superior
                } else {
                    obstacleY = this.size.height - obstacleHeight  // Obstáculo en la parte inferior
                }
                obstacleHeight = (100..300).random().toFloat()  // Tamaño aleatorio
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures {
                        velocity = -10f
                    }
                }
        ) {
            // Asigna el valor inicial de birdX solo una vez
            if (birdX == 0f) {
                birdX = size.width / 2 - birdWidth / 2
            }

            // Dibujar personaje con hitbox ajustada
            drawImage(
                image = birdImage,
                topLeft = Offset(x = birdX, y = birdY - birdHeight / 2)
            )


            // Dibuja la hitbox del pájaro con dimensiones reducidas
            drawRect(
                color = androidx.compose.ui.graphics.Color.Red,
                topLeft = Offset(birdX + birdWidth * 0.2f, birdY - birdHeight * 0.4f),
                size = androidx.compose.ui.geometry.Size(birdWidth * 0.6f, birdHeight * 0.6f)
            )

            // Dibujar obstáculo con hitbox ajustada
            drawImage(
                image = obstacleImage,
                topLeft = Offset(x = obstacleX, y = obstacleY)
            )


            // Dibuja la hitbox del obstáculo con dimensiones reducidas
            drawRect(
                color = androidx.compose.ui.graphics.Color.Blue,
                topLeft = Offset(obstacleX + obstacleWidth * 0.2f, obstacleY),
                size = androidx.compose.ui.geometry.Size(obstacleWidth * 0.6f, obstacleHeight)
            )

            // Check de colisión con hitboxes
            if (checkCollision(
                    birdX = birdX + birdWidth * 0.2f,
                    birdY = birdY - birdHeight * 0.4f,
                    birdWidth = birdWidth * 0.6f,
                    birdHeight = birdHeight * 0.6f,
                    obstacleX = obstacleX + obstacleWidth * 0.2f,
                    obstacleY = obstacleY,
                    obstacleWidth = obstacleWidth * 0.6f,
                    obstacleHeight = obstacleHeight
                )
            ) {
                isGameOver = true
                db.insertScore(score)  // Guarda el puntaje al finalizar
                highScores = db.getTopScores()  // Actualiza la lista de los 5 puntajes más altos
            }

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

        // Mostrar puntaje actual
        Text(
            text = "Puntaje: $score",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        )

        // Mostrar los 5 mejores puntajes al finalizar el juego
        if (isGameOver) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 150.dp)
            ) {
                Text("Top 5 Puntajes:", modifier = Modifier.padding(8.dp))
                highScores.forEach { highScore ->
                    Text(text = "$highScore", modifier = Modifier.padding(4.dp))
                }
            }

            Button(
                onClick = {
                    birdY = 0f
                    velocity = 0f
                    obstacleX = 1000f
                    score = 0
                    isGameOver = false
                },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("Reintentar")
            }
        }
    }
}


// Función para verificar colisión de hitboxes
fun checkCollision(
    birdX: Float,
    birdY: Float,
    birdWidth: Float,
    birdHeight: Float,
    obstacleX: Float,
    obstacleY: Float,
    obstacleWidth: Float,
    obstacleHeight: Float
): Boolean {
    return birdX < obstacleX + obstacleWidth &&
            birdX + birdWidth > obstacleX &&
            birdY < obstacleY + obstacleHeight &&
            birdY + birdHeight > obstacleY
}


@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    PruebaTheme {
        GameScreen()
    }
}
