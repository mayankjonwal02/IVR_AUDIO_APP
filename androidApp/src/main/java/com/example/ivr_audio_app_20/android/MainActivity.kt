package com.example.ivr_audio_app.android

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.ivr_audio_app_20.android.permission.getPermissions
import com.example.ivr_audio_app_20.android.text_to_speech

import com.example.ivr_call_app_20.android.Bluetooth.BluetoothViewModel
import createdirectory
import filepathui
import responceui

lateinit var mybluetooth : BluetoothViewModel


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    getPermissions()
                    var context = LocalContext.current
                    mybluetooth   = BluetoothViewModel()
                    mybluetooth.initialiseBluetooth(context)
                   commonui()
//                        text_to_speech()
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()

        Log.i("audioservice","Service stopped automatically")
        var intent = Intent(this,myaudioservice::class.java)
        this.stopService(intent)

    }

//
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
