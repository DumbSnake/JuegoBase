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

    var birdY by remember { mutableStateOf(0f) }
    var velocity by remember { mutableStateOf(0f) }
    var obstacleX by remember { mutableStateOf(1000f) }
    var isGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var scoreList by remember { mutableStateOf(emptyList<Int>()) }

    val birdImage = ImageBitmap.imageResource(id = R.drawable.boy)
    val obstacleImage = ImageBitmap.imageResource(id = R.drawable.edificio)

    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (!isGameOver) {
                delay(16L)
                velocity += 0.5f
                birdY += velocity
                obstacleX -= 5f
                score += 1

                if (obstacleX < 0) {
                    obstacleX = 1000f
                }

                if (birdY > 1000f || birdY < 0f || (obstacleX in 450f..550f && birdY in 300f..700f)) {
                    isGameOver = true
                    db.insertScore(score)
                    scoreList = db.getAllScores()
                }
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
            drawImage(
                image = birdImage,
                topLeft = Offset(x = size.width / 2 - birdImage.width / 2, y = birdY - birdImage.height / 2)
            )
            drawImage(
                image = obstacleImage,
                topLeft = Offset(x = obstacleX, y = 300f)
            )
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

        Text(
            text = "Puntaje: $score",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        if (isGameOver) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Puntajes Anteriores:", fontSize = 20.sp)
                scoreList.forEachIndexed { index, score ->
                    Text(text = "${index + 1}. $score", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    birdY = 0f
                    velocity = 0f
                    obstacleX = 1000f
                    score = 0
                    isGameOver = false
                }) {
                    Text("Reintentar")
                }
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
