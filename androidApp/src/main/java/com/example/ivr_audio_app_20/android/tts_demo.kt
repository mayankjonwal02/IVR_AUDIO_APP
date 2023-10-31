package com.example.ivr_audio_app_20.android

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun text_to_speech() {
    val context = LocalContext.current
    val tts = remember {
        TextToSpeech(context, null)
    }

    tts.language = Locale("en", "IN")

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
            val currentDateAndTime = System.currentTimeMillis().toString()
            val utteranceId = null //"Date_$currentDateAndTime"
            tts.speak("hello", TextToSpeech.QUEUE_ADD, null, utteranceId)
            tts.speak("my", TextToSpeech.QUEUE_ADD, null, utteranceId)
            tts.speak("name", TextToSpeech.QUEUE_ADD, null, utteranceId)
            tts.speak("is", TextToSpeech.QUEUE_ADD, null, utteranceId)
            tts.speak("Mayank", TextToSpeech.QUEUE_ADD, null, utteranceId)
        }) {
            Text("Speak 10 Times")
        }
    }
}
