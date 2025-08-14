package com.example.lifesaver.screens

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.util.Locale

@Composable
fun EmergencyTextInputScreen(navController: NavController) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
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

    var typedText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    fun handleTypedMessage(message: String) {
        if (message.contains("help", ignoreCase = true) || message.contains(
                "sos",
                ignoreCase = true
            )
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        500,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )

                ttsRef.value?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId : String?) {}
                    override fun onDone(utteranceId: String?) {
                        if(utteranceId == "SOS_MESSAGE"){
                            (context as Activity).runOnUiThread {
                                navController.navigate("dashboard"){
                                    popUpTo("voicetrigger"){inclusive = true}
                                }
                            }
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e("TTS", "Error occurred in TTS")
                    }
                })
                val params = Bundle()
                params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOS_MESSAGE")
                ttsRef.value?.speak(
                //tts.speak(
                    "Okay, we are sending emergency help!",
                    TextToSpeech.QUEUE_FLUSH,
                    params,
                    "SOS_MESSAGE"
                )
            } else {
                showError = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Type your emergency message:", style = MaterialTheme.typography.titleMedium , color = Color.Black)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = typedText,
                onValueChange = {
                    typedText = it
                    showError = false
                },
                label = { Text("Enter message..." , color = Color.Red) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color.Red
                ),
                textStyle = TextStyle(color = Color.Black)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { handleTypedMessage(typedText) },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(0.8f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(6.dp)
            ) {
                Text("Send", fontSize = 16.sp)
            }
            if (showError) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Message must contain 'help' or 'sos'", color = Color.Red)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyTextInputScreenPreview(){
    EmergencyTextInputScreen(navController = rememberNavController())
}
