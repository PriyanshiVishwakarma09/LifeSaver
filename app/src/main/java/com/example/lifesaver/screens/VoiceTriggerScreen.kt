package com.example.lifesaver.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lifesaver.R
import java.util.*

@Composable
fun VoiceTriggerScreen(navController: NavController) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Tap the mic and say 'Help' or 'SOS'") }
    var micPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val speechRecognizer = remember {
        mutableStateOf<SpeechRecognizer?>(null)
    }

    val speechIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
    }

    val tts = remember {
        mutableStateOf<TextToSpeech?>(null)
    }

    LaunchedEffect(Unit) {
        tts.value = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale.ENGLISH
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.value?.stop()
            tts.value?.shutdown()
            speechRecognizer.value?.destroy()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        micPermissionGranted = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Microphone permission is required for voice commands.", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!micPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun vibrateAndNavigate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(800)
        }
        navController.navigate("dashboard") {
            popUpTo("voicetrigger") { inclusive = true }
        }
    }

    fun handleResult(text: String) {
        if (text.lowercase().contains("help") || text.lowercase().contains("sos")) {
            resultText = "Okay, we are sending emergency help!"
            tts.value?.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, "tts1")
            tts.value?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    vibrateAndNavigate()
                }
                override fun onError(utteranceId: String?) {
                    vibrateAndNavigate()
                }
            })
        } else {
            resultText = "Please say 'help' or 'SOS' clearly."
            tts.value?.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, "tts2")
        }
    }

    fun startListening() {
        if (micPermissionGranted) {
            isListening = true
            resultText = "Listening..."
            if (speechRecognizer.value == null) {
                speechRecognizer.value = SpeechRecognizer.createSpeechRecognizer(context)
            }
            speechRecognizer.value?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    isListening = false
                }
                override fun onError(error: Int) {
                    isListening = false
                    val message = when (error) {
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech found"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout, try again."
                        else -> "Speech recognition error: $error"
                    }
                    resultText = message
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        handleResult(matches[0])
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            speechRecognizer.value?.startListening(speechIntent)
        } else {
            Toast.makeText(context, "Microphone permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    val scrollable = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollable)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .border(
                            width = 0.7.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(70.dp)
                        )
                        .clip(shape = RoundedCornerShape(70.dp))
                        .size(50.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Voice Help",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1C),
                    letterSpacing = 1.5.sp,
                    fontStyle = FontStyle.Normal,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = resultText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isListening) {
                        startListening()
                    },
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mic),
                    contentDescription = "Mic",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
            EmergencyTextInputScreen(navController)
        }
    }
}
