package com.example.ivr_audio_app.android

//package com.example.ivr_calling_app.android

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.database.FirebaseDatabase
import java.lang.reflect.Modifier

@Preview
@Composable
fun serviceui() {
    var mytext by remember {
        mutableStateOf("service stopped")
    }
    var context = LocalContext.current

    fun manipulate()
    {
        if(mytext == "service stopped")
        {

                    mytext = "service started"
                    var intent = Intent(context,myaudioservice::class.java)
                    context.startService(intent)


        }
        else
        {

                    mytext = "service stopped"
                    var intent = Intent(context,myaudioservice::class.java)
                    context.stopService(intent)

        }
    }

    Box(modifier = androidx.compose.ui.Modifier
        .fillMaxSize()
        .background(Color.White), contentAlignment = Alignment.Center)
    {

        Button(onClick = { manipulate() }) {
            Text(text = "audio " + mytext)
        }
    }




}