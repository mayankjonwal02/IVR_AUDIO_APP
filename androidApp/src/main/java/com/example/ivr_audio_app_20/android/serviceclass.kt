package com.example.ivr_audio_app.android;

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.compose.ui.text.toUpperCase
import com.example.ivr_call_app_20.android.Bluetooth.msgupdate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.*

data class cat_lan( var category : String , var language : String  , var duedate : String )

var mediaPlayer : MediaPlayer? = null
class myaudioservice : Service()
{


    var tts: TextToSpeech? = null
    var job : Job? = null
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    override fun onCreate() {
        super.onCreate()
        mediaPlayer = null
        tts = TextToSpeech(this@myaudioservice ) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TTS engine is initialized successfully
            }
        }
        tts!!.language = Locale.ENGLISH



    }

    private var isServiceRunning = false

    val _isrunning =  MutableStateFlow<Int>(0)
    val isrunning : StateFlow<Int> = _isrunning




    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isServiceRunning) {
            // Mark the service as running to prevent concurrent calls
            isServiceRunning = true
            Log.v("audioservice", "audio service started")
            Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show()
            var key = false


            job = CoroutineScope(Dispatchers.Main).launch{




                mybluetooth.mymessage.collect { msgupdate ->


                        if(msgupdate.key == 3)
                        {

                            if (mediaPlayer == null && isrunning.value == 0) {

                                    _isrunning.value = 1
                                    var patientInfo =
                                        Gson().fromJson(msgupdate.message, cat_lan::class.java)

                                    var duedate = patientInfo.duedate
                                    var maindirname = "IVR_AUDIO_LOCATION"
                                    var subdirname = "xyz"
                                    var audiofilename = "xyz.mp3"


                                    Toast.makeText(
                                        this@myaudioservice,
                                        patientInfo.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    subdirname = patientInfo.category.toUpperCase()
                                    audiofilename = "${patientInfo.language.toUpperCase()}.mp3"

                                    var externaldir = File(
                                        this@myaudioservice.getExternalFilesDir(null),
                                        maindirname
                                    )
                                    var subdir = File(externaldir, subdirname)
                                    if (subdir.exists() && externaldir.exists() && !key) {

                                        val audiofile = File(
                                            subdir,
                                            audiofilename
                                        )
                                        if (audiofile.exists()) {
                                            try {
                                                mediaPlayer?.release()
                                                mediaPlayer = MediaPlayer.create(
                                                    this@myaudioservice,
                                                    Uri.fromFile(audiofile)
                                                )
                                                mediaPlayer?.setOnCompletionListener {
                                                    tts?.setOnUtteranceCompletedListener{id ->
                                                        if(id == "audioapp"){
                                                            key = false
                                                            mediaPlayer?.release()

                                                            mybluetooth.shareit.write(
                                                                msgupdate(
                                                                    4,
                                                                    "Perfectly Responded"
                                                                )
                                                            )
                                                            mybluetooth._mymessage.value =
                                                                msgupdate(0, "")

                                                            mediaPlayer = null

                                                            _isrunning.value = 0
                                                        }
                                                    }
                                                    tts!!.speak(duedate,TextToSpeech.QUEUE_ADD,null , "audioapp" )


//                                                    key = false
//                                                    mediaPlayer?.release()
//
//                                                    mybluetooth.shareit.write(
//                                                        msgupdate(
//                                                            4,
//                                                            "Perfectly Responded"
//                                                        )
//                                                    )
//                                                    mybluetooth._mymessage.value = msgupdate(0, "")
//
//                                                    mediaPlayer = null
//
//                                                    _isrunning.value = 0

                                                }
                                                Toast.makeText(
                                                    this@myaudioservice,
                                                    "Playing audio",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                mediaPlayer?.start()
                                                key = true


                                            } catch (e: Exception) {

                                                Toast.makeText(
                                                    this@myaudioservice,
                                                    "Error playing audio",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                                mybluetooth.shareit.write(msgupdate(4, "Error"))
                                                mybluetooth._mymessage.value = msgupdate(0, "")
                                                key = false
                                                _isrunning.value = 0
                                            }

                                        } else {

                                            Toast.makeText(
                                                this@myaudioservice,
                                                "Audio file doesn't exist",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                            mybluetooth.shareit.write(
                                                msgupdate(
                                                    4,
                                                    "Audio doesn't Exist"
                                                )
                                            )
                                            mybluetooth._mymessage.value = msgupdate(0, "")
                                            key = false
                                            _isrunning.value = 0
                                        }
                                    } else {

                                        Toast.makeText(
                                            this@myaudioservice,
                                            "Category doesn't exist",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                        mybluetooth.shareit.write(
                                            msgupdate(
                                                4,
                                                "Category doesn't Exist"
                                            )
                                        )
                                        mybluetooth._mymessage.value = msgupdate(0, "")
                                        key = false
                                        _isrunning.value = 0
                                    }



                            }
                        }
                    if(msgupdate == msgupdate(2,"disconnected"))
                    {

                        if(mediaPlayer != null){
                            mediaPlayer?.stop()
                            mediaPlayer?.release()
                            mediaPlayer = null
                            mybluetooth.shareit.write(msgupdate(4, "Listened Half"))
                            mybluetooth._mymessage.value = msgupdate(0, "")
                            key = false
                            _isrunning.value = 0
                        }

                    }


                }
            }


            return super.onStartCommand(intent, flags, startId)
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        isServiceRunning = false
        Log.v("audioservice","audio service stopped")
        job?.cancel()
        job = null
        tts = null
        super.onDestroy()

    }

}


