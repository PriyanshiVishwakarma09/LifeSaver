package com.example.lifesaver.screens

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifesaver.R
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController


@Composable
fun VoiceTriggerScreen(navController: NavController) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("Tap the mic and say 'Help or 'SOS'") }
    var micPermissionGranted by remember { mutableStateOf(false) }
    val ttsRef = remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = ttsRef.value?.setLanguage(Locale.ENGLISH)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language specified is not supported!")
                }
            }
        }
        ttsRef.value = tts
    }

    DisposableEffect(Unit) {
        onDispose {
            ttsRef.value?.shutdown()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        micPermissionGranted = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Microphone permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
    val speechRecognizer = remember(micPermissionGranted) {
        if (micPermissionGranted) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    val speechIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something like 'Help'")
        }
    }

    //  @RequiresApi(Build.VERSION_CODES.O)
    fun vibrateAndNavigate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(800)
        }
        navController.navigate("sos_screen")
    }

    fun handleResult(text: String) {
        if (text.lowercase().contains("help") || text.lowercase().contains("sos")) {
            resultText = "Okay, we are sending emergency help!"
            ttsRef.value?.speak(resultText, TextToSpeech.QUEUE_FLUSH, null, null)
            vibrateAndNavigate()
        } else {
            resultText = "Please say 'help' or 'SOS' clearly."
        }
    }

    LaunchedEffect(Unit) {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                    else -> "Unknown error"
                }
                Toast.makeText(context, "Speech error: $message", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(results: Bundle?) {
                isListening = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let {
                    handleResult(it[0])
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }
    val scrollable = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(scrollable)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.height(80.dp)
                .fillMaxWidth()
                .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically ,
                horizontalArrangement = Arrangement.Start) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .padding(end = 16.dp)
//                    .align(Alignment.TopCenter)
                        .border(width = 0.7.dp , color = Color.LightGray , shape = RoundedCornerShape(70.dp))
                        .clip(shape = RoundedCornerShape(70.dp))
                        .size(50.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Voice Help",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1C), // Dark gray for modern feel
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

            Column(modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(color = Color.LightGray)) {  }

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
//                        .clip(CircleShape)
                        .background(Color.White)
//                        .shadow(8.dp, CircleShape, clip = false)
                        .clickable {
                            if (!isListening) {
                                isListening = true
                                resultText = "Listening..."
                                speechRecognizer?.startListening(speechIntent)
                            } else if (!micPermissionGranted) {
                                resultText = "Microphone permission not granted"
                            }
                        },
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mic),
                        contentDescription = "Mic",
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Fit
//                .clickable {
//                    if (!isListening){
//                        isListening = true
//                        resultText = "Listening..."
//                        speechRecognizer?.startListening(speechIntent)
//                    } else if(!micPermissionGranted){
//                        resultText = "Microphone permission not granted"
//                    }
//                }
                    )
                }
                Spacer(modifier = Modifier.height(100.dp))
                // Text("Or type your emergency below:")
                EmergencyTextInputScreen(navController)
            }

        }


}

@Preview(showBackground = true)
@Composable
fun PreviewFunction(){
    VoiceTriggerScreen(navController = rememberNavController())
}
