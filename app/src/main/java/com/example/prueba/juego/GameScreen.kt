package com.example.prueba.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prueba.R
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GameScreen(onGameOver: (Int) -> Unit, selectedSkin: Int) {
    var score by remember { mutableStateOf(0) }
    var characterPosition by remember { mutableStateOf(300f) }
    var velocity by remember { mutableStateOf(0f) }
    var obstacles by remember { mutableStateOf(generateObstacles()) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }

    // Configuraciones de personaje
    val characterWidth = 50.dp
    val characterHeight = 50.dp
    val characterXPosition = 100f

    // Configuración de obstáculos
    val obstacleWidth = 80.dp
    val gapSize = 200.dp

    // Límite superior e inferior
    val screenHeight = 600.dp

    // Reinicio del juego
    fun resetGame() {
        score = 0
        characterPosition = 300f
        velocity = 0f
        obstacles = generateObstacles()
        isGameOver = false
        isPlaying = true
    }

    // Lógica principal del juego
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(16L) // Aproximadamente 60 FPS

            if (!isGameOver) {
                // Aplicar gravedad
                velocity += 1.5f
                characterPosition += velocity

                // Límite superior e inferior
                if (characterPosition <= 0 || characterPosition >= screenHeight.value) {
                    isGameOver = true
                }

                // Actualizar obstáculos
                obstacles = obstacles.map { obstacle ->
                    if (obstacle.x < -obstacleWidth.value) {
                        score++
                        Obstacle(
                            x = 800f,
                            gapY = Random.nextInt(150, 450).toFloat()
                        )
                    } else {
                        obstacle.copy(x = obstacle.x - 5f)
                    }
                }

                // Verificar colisiones
                if (checkCollision(obstacles, characterXPosition, characterPosition)) {
                    isGameOver = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    if (!isGameOver) {
                        velocity = -15f
                    }
                }
            }
    ) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.fondox),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Obstáculos
        obstacles.forEach { obstacle ->
            // Obstáculo superior con imagen
            Image(
                painter = painterResource(id = R.drawable.pngegg),
                contentDescription = "Top Obstacle",
                modifier = Modifier
                    .offset(x = obstacle.x.dp, y = 0.dp)
                    .size(obstacleWidth, (obstacle.gapY.dp - (gapSize / 2))),
                contentScale = ContentScale.Crop
            )

            // Obstáculo inferior con imagen
            Image(
                painter = painterResource(id = R.drawable.pngegg),
                contentDescription = "Bottom Obstacle",
                modifier = Modifier
                    .offset(x = obstacle.x.dp, y = (obstacle.gapY.dp + (gapSize / 2)))
                    .size(obstacleWidth, (screenHeight - (obstacle.gapY.dp + (gapSize / 2)))),
                contentScale = ContentScale.Crop
            )
        }

        // Personaje (imagen y hitbox)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(x = characterXPosition.dp, y = characterPosition.dp - (characterHeight / 2))
                .size(characterWidth, characterHeight)
        ) {
            Image(
                painter = painterResource(id = selectedSkin),
                contentDescription = "Character",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        // Mostrar puntuación
        Text(
            text = "Puntos: $score",
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )

        // Pantalla de "Game Over"
        if (isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡Juego Terminado!",
                    fontSize = 32.sp,
                    color = Color.White
                )
                Text(
                    text = "Puntaje: $score",
                    fontSize = 24.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { resetGame() }) {
                    Text("Reintentar")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { onGameOver(score) }) {
                    Text("Salir")
                }
            }
        }
    }
}

// Clase de datos para los obstáculos
data class Obstacle(val x: Float, val gapY: Float)

// Generar obstáculos iniciales
fun generateObstacles(): List<Obstacle> {
    return List(3) { i ->
        Obstacle(
            x = 800f + i * 400f,
            gapY = Random.nextInt(150, 450).toFloat()
        )
    }
}

// Verificar colisiones
fun checkCollision(
    obstacles: List<Obstacle>,
    characterX: Float,
    characterY: Float
): Boolean {
    val characterWidth = 50f
    val characterHeight = 50f

    return obstacles.any { obstacle ->
        val gapTop = obstacle.gapY - 100f
        val gapBottom = obstacle.gapY + 100f

        (characterX + characterWidth > obstacle.x && characterX < obstacle.x + 100f) &&
                (characterY < gapTop || characterY + characterHeight > gapBottom)
    }
}
