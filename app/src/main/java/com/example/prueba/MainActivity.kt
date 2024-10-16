package com.example.prueba

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prueba.ui.theme.PruebaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Solicita permiso de audio
        requestAudioPermission()

        setContent {
            PruebaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }

    private val RECORD_AUDIO_REQUEST_CODE = 123

    fun requestAudioPermission() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST_CODE)
        }
    }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    var birdY by remember { mutableStateOf(0f) }  // Posición del pájaro en Y
    var velocity by remember { mutableStateOf(0f) }  // Velocidad del pájaro
    var obstacleX by remember { mutableStateOf(1000f) }  // Posición del obstáculo en X
    var isGameOver by remember { mutableStateOf(false) }
    var isRunning by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    // Detecta el sonido en un hilo separado si el juego está en ejecución
    if (isRunning) {
        LaunchedEffect(Unit) {
            coroutineScope.launch(Dispatchers.Default) {
                startAudioDetection { isLoud ->
                    if (isLoud && !isGameOver) {
                        velocity = -10f  // El pájaro salta cuando el sonido es lo suficientemente alto
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            // Ciclo de juego
            while (!isGameOver && isRunning) {
                delay(16L)  // Aproximadamente 60 FPS
                velocity += 0.5f  // Simula la gravedad
                birdY += velocity
                obstacleX -= 5f  // Movimiento del obstáculo hacia la izquierda

                // Reinicia la posición del obstáculo cuando sale de la pantalla
                if (obstacleX < 0) {
                    obstacleX = 1000f
                }

                // Verifica colisiones (simplificado)
                if (birdY > 1000f || birdY < 0f || (obstacleX in 450f..550f && birdY in 300f..700f)) {
                    isGameOver = true
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Dibuja el juego si está en ejecución
        if (isRunning) {
            Canvas(modifier = modifier.fillMaxSize()) {
                // Dibuja el pájaro
                drawCircle(
                    color = Color.Red,
                    radius = 30f,
                    center = Offset(x = size.width / 2, y = birdY)
                )

                // Dibuja el obstáculo
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(x = obstacleX, y = 300f),
                    size = Size(100f, 400f)
                )
            }
        }

        // Si el juego termina, muestra el mensaje "Game Over" y el botón de "Reintentar"
        if (isGameOver) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Game Over", color = Color.Red)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    // Reinicia las variables del juego
                    birdY = 0f
                    velocity = 0f
                    obstacleX = 1000f
                    isGameOver = false
                    isRunning = true
                }) {
                    Text(text = "Reintentar")
                }
            }
        }
    }
}

// Función que captura el sonido y ejecuta un callback cuando el nivel de sonido supera un umbral
@SuppressLint("MissingPermission")
fun startAudioDetection(onSoundDetected: (isLoud: Boolean) -> Unit) {
    val bufferSize = AudioRecord.getMinBufferSize(
        44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
    )

    val audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize
    )

    val buffer = ShortArray(bufferSize)

    audioRecord.startRecording()

    while (true) {
        val readSize = audioRecord.read(buffer, 0, bufferSize)

        // Calcula el nivel de sonido (root mean square, RMS)
        var sum = 0.0
        for (i in 0 until readSize) {
            sum += buffer[i] * buffer[i].toDouble()
        }
        val rms = Math.sqrt(sum / readSize)

        // Umbral de volumen para determinar si el sonido es lo suficientemente alto
        val isLoud = rms > 2000  // Ajusta este valor según sea necesario

        onSoundDetected(isLoud)
    }
}


@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    PruebaTheme {
        GameScreen()
    }
}
