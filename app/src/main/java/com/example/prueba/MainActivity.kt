package com.example.prueba

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
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

    // Variables para el tamaño de la pantalla
    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }

    // Cargar puntajes al iniciar el juego
    LaunchedEffect(Unit) {
        highScores = db.getTopScores()  // Obtener los 5 puntajes más altos
    }

    LaunchedEffect(isGameOver) {
        while (!isGameOver) {
            delay(16L)
            velocity += 0.5f
            birdY += velocity

            birdY = birdY.coerceIn(0f, screenHeight - birdHeight)
            obstacleX -= 5f

            // Incrementar puntaje solo si no es "Game Over"
            if (!isGameOver) {
                score += 1
            }

            if (obstacleX < -obstacleWidth) {
                obstacleX = screenWidth
                obstacleY = if (Math.random() < 0.5) 0f else screenHeight - obstacleHeight
                obstacleHeight = (100..300).random().toFloat()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.fondox),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size ->
                    screenWidth = size.width.toFloat()
                    screenHeight = size.height.toFloat()
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        velocity = -10f
                    }
                }
        ) {
            if (birdX == 0f) {
                birdX = screenWidth / 2 - birdWidth / 2
            }

            drawImage(
                image = birdImage,
                topLeft = Offset(x = birdX, y = birdY)
            )

            drawRect(
                color = Color.Red,
                topLeft = Offset(birdX, birdY),
                size = Size(birdWidth, birdHeight)
            )

            drawImage(
                image = obstacleImage,
                topLeft = Offset(x = obstacleX, y = obstacleY)
            )

            drawRect(
                color = Color.Blue,
                topLeft = Offset(obstacleX, obstacleY),
                size = Size(obstacleWidth, obstacleHeight)
            )

            if (checkCollision(
                    birdX = birdX,
                    birdY = birdY,
                    birdWidth = birdWidth,
                    birdHeight = birdHeight,
                    obstacleX = obstacleX,
                    obstacleY = obstacleY,
                    obstacleWidth = obstacleWidth,
                    obstacleHeight = obstacleHeight
                )
            ) {
                if (!isGameOver) {
                    isGameOver = true
                    db.insertScore(score)
                    highScores = db.getTopScores()  // Actualizar los puntajes más altos
                }
            }

            if (isGameOver) {
                drawContext.canvas.nativeCanvas.drawText(
                    "Game Over",
                    screenWidth / 2,
                    screenHeight / 2,
                    android.graphics.Paint().apply {
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 64f
                        color = android.graphics.Color.RED
                    }
                )
            }
        }

        Text(
            text = "Puntaje: $score",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .background(color = Color(0xFF6200EE), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (isGameOver) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp)
                    .shadow(4.dp, RoundedCornerShape(8.dp))
                    .background(color = Color(0xFF6200EE), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Top 5 Puntajes:",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                highScores.forEach { highScore ->
                    Text(
                        text = "$highScore",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

            Button(
                onClick = {
                    birdY = 0f
                    velocity = 0f
                    obstacleX = screenWidth
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
